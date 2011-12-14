package cn.com.mozilla.sync.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

/**
 * @author Yuan Xulei
 */
public class HttpsTransport {

  private static final int              HTTPS_PORT_DEFAULT  = 443;
  private static final String           USER_AGENT          = "firefoxmini/1.0";
  private static final boolean          ALLOW_INVALID_CERTS = true;

  private static final HttpParams       sHttpParams;

  static {
    HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(params, "UTF-8");
    HttpProtocolParams.setHttpElementCharset(params, "UTF-8");
    HttpProtocolParams.setUserAgent(params, USER_AGENT);
    HttpConnectionParams.setConnectionTimeout(params, 8000);
    HttpConnectionParams.setSoTimeout(params, 10000);
    // Expect-Continue is only needed when your request is large (like file
    // uploading) and the server may have authorization requirement. You
    // don't want send a huge file and get a Access Denied error. So you
    // just send the headers first and if the server says continue, you will
    // then send the whole request.
    // http://stackoverflow.com/questions/2795320/httpprotocolparams-setuseexpectcontinueparams-false-when-to-set-true
    HttpProtocolParams.setUseExpectContinue(params, true);
    sHttpParams = params;
  }

  private SocketFactory                 mSslSocketFactory   = null;

  private final ClientConnectionManager mClientConMgr;

  public HttpsTransport() {
    // Create SSL socket factory
    if (ALLOW_INVALID_CERTS) {

      try {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        mSslSocketFactory = new EasySSLSocketFactory(trustStore);
      } catch (GeneralSecurityException e) {
        Log.w("Firefoxmini", e.toString());
      } catch (IOException e) {
        Log.w("Firefoxmini", e.toString());
      }
    }
    if (mSslSocketFactory == null) {
      mSslSocketFactory = SSLSocketFactory.getSocketFactory();
    }

    // Create ClientConnectionManager
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("https", mSslSocketFactory,
        HTTPS_PORT_DEFAULT));
    mClientConMgr = new SingleClientConnManager(sHttpParams, schemeRegistry);
  }

  public void shutdown() {
    mClientConMgr.shutdown();
  }

  public HttpResponse execHttpMethod(HttpRequestBase method) throws IOException {
    return execHttpMethod(method, "", "");
  }

  public HttpResponse execHttpMethod(HttpRequestBase method, String userName,
      String passWord) throws IOException {
    DefaultHttpClient client = new DefaultHttpClient(mClientConMgr, sHttpParams);
    if (userName.length() > 0 && passWord.length() > 0) {
      Credentials defaultcreds = new UsernamePasswordCredentials(userName,
          passWord);
      client.getCredentialsProvider().setCredentials(AuthScope.ANY,
          defaultcreds);
    }

    // retry 3 times
    DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(
        3, true);
    client.setHttpRequestRetryHandler(retryHandler);

    HttpResponse response = null;
    try {
      response = client.execute(method);
    } catch (IllegalStateException e) {
      // Deals with the situation that ClientConnectionManager shuts down during
      // connection
      throw new IOException(e.toString());
    }
    return response;

  }

  // Trusts all certificates
  private static class EasySSLSocketFactory extends SSLSocketFactory {
    SSLContext mSslContext;

    public EasySSLSocketFactory(KeyStore truststore)
        throws KeyManagementException, NoSuchAlgorithmException,
        KeyStoreException, UnrecoverableKeyException {
      super(truststore);

      TrustManager tm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      };

      mSslContext = SSLContext.getInstance("TLS");
      mSslContext.init(null, new TrustManager[] { tm }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
        boolean autoClose) throws IOException, UnknownHostException {
      return mSslContext.getSocketFactory().createSocket(socket, host, port,
          autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
      return mSslContext.getSocketFactory().createSocket();
    }

  }
}
