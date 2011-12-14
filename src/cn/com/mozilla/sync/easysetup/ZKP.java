package cn.com.mozilla.sync.easysetup;

import java.math.BigInteger;
import java.security.MessageDigest;

import cn.com.mozilla.sync.utils.BigIntegerHelper;
import cn.com.mozilla.sync.utils.RandomHelper;

public class ZKP {
  public BigInteger gr = BigInteger.ZERO; // g^r, r is a random number
  public BigInteger b  = BigInteger.ZERO; // b = r - x*h, h=hash(g, g^r, g^x,

  // signer)

  public static boolean verify(BigInteger p, BigInteger q, BigInteger g,
      BigInteger gx, ZKP zkp_x, String signerID) {
    /* sig={g^r, b} */
    BigInteger h = hash(g, zkp_x.gr, gx, signerID);
    if (gx.compareTo(BigInteger.ZERO) == 1 && // g^x > 0
        gx.compareTo(p.subtract(BigInteger.ONE)) == -1 && // g^x < p-1
        gx.modPow(q, p).compareTo(BigInteger.ONE) == 0 && // g^x^q = 1
        /*
         * Below, I took an straightforward way to compute g^b * g^x^h, which
         * needs 2 exp. Using a simultaneous computation technique would only
         * need 1 exp.
         */
        g.modPow(zkp_x.b, p).multiply(gx.modPow(h, p)).mod(p)
            .compareTo(zkp_x.gr) == 0) // g^r=g^x * g^x^h
      return true;
    else
      return false;
  }

  public ZKP() {
  }

  public ZKP(BigInteger p, BigInteger q, BigInteger g, BigInteger gx,
      BigInteger x, String signerID) {
    this(p, q, g, gx, x, signerID, RandomHelper.getRandomBigInteger(q));
  }

  /**
   * Generate a ZKP whose r value is not random but constant for unit test
   */
  public ZKP(BigInteger p, BigInteger q, BigInteger g, BigInteger gx,
      BigInteger x, String signerID, BigInteger r) {
    gr = g.modPow(r, p);
    BigInteger h = hash(g, gr, gx, signerID); // h
    b = r.subtract(x.multiply(h).mod(q)).mod(q); // b = r-x*h
  }

  private static BigInteger hash(BigInteger g, BigInteger gr, BigInteger gx,
      String signerID) {
    MessageDigest sha = null;
    byte[] md = null;

    try {
      sha = MessageDigest.getInstance("SHA-256");
      sha.reset();

      /*
       * Note: you should ensure the items in H(...) have clear boundaries. It
       * is simple if the other party knows sizes of g, gr, gx and signerID and
       * hence the boundary is unambiguous. If not, you'd better prepend each
       * item with its byte length, but I've omitted that here.
       */

      hashByteArrayWithLength(sha,
          BigIntegerHelper.BigIntegerToByteArrayWithoutSign(g));
      hashByteArrayWithLength(sha,
          BigIntegerHelper.BigIntegerToByteArrayWithoutSign(gr));
      hashByteArrayWithLength(sha,
          BigIntegerHelper.BigIntegerToByteArrayWithoutSign(gx));
      hashByteArrayWithLength(sha, signerID.getBytes());
      md = sha.digest();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return BigIntegerHelper.ByteArrayToBigIntegerWithoutSign(md);
  }

  private static void hashByteArrayWithLength(MessageDigest sha, byte[] data) {
    int length = data.length;
    byte[] b = new byte[] { (byte) (length >>> 8), (byte) (length & 0xff) };
    sha.update(b);
    sha.update(data);
  }
}
