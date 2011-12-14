package cn.com.mozilla.sync;

import java.math.BigInteger;
import java.security.MessageDigest;

import android.test.AndroidTestCase;
import cn.com.mozilla.sync.easysetup.ZKP;

public class ZKPTest extends AndroidTestCase {
  BigInteger p;
  BigInteger q;
  BigInteger g;

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    p = new BigInteger(
        "90066455B5CFC38F9CAA4A48B4281F292C260FEEF01FD61037E56258A7795A1C7AD46076982CE6BB956936C6AB4DCFE05E6784586940CA544B9B2140E1EB523F009D20A7E7880E4E5BFA690F1B9004A27811CD9904AF70420EEFD6EA11EF7DA129F58835FF56B89FAA637BC9AC2EFAAB903402229F491D8D3485261CD068699B6BA58A1DDBBEF6DB51E8FE34E8A78E542D7BA351C21EA8D8F1D29F5D5D15939487E27F4416B0CA632C59EFD1B1EB66511A5A0FBF615B766C5862D0BD8A3FE7A0E0DA0FB2FE1FCB19E8F9996A8EA0FCCDE538175238FC8B0EE6F29AF7F642773EBE8CD5402415A01451A840476B2FCEB0E388D30D4B376C37FE401C2A2C2F941DAD179C540C1C8CE030D460C4D983BE9AB0B20F69144C1AE13F9383EA1C08504FB0BF321503EFE43488310DD8DC77EC5B8349B8BFE97C2C560EA878DE87C11E3D597F1FEA742D73EEC7F37BE43949EF1A0D15C3F3E3FC0A8335617055AC91328EC22B50FC15B941D3D1624CD88BC25F3E941FDDC6200689581BFEC416B4B2CB73",
        16);
    q = new BigInteger(
        "CFA0478A54717B08CE64805B76E5B14249A77A4838469DF7F7DC987EFCCFB11D", 16);
    g = new BigInteger(
        "5E5CBA992E0A680D885EB903AEA78E4A45A469103D448EDE3B7ACCC54D521E37F84A4BDD5B06B0970CC2D2BBB715F7B82846F9A0C393914C792E6A923E2117AB805276A975AADB5261D91673EA9AAFFEECBFA6183DFCB5D3B7332AA19275AFA1F8EC0B60FB6F66CC23AE4870791D5982AAD1AA9485FD8F4A60126FEB2CF05DB8A7F0F09B3397F3937F2E90B9E5B9C9B6EFEF642BC48351C46FB171B9BFA9EF17A961CE96C7E7A7CC3D3D03DFAD1078BA21DA425198F07D2481622BCE45969D9C4D6063D72AB7A0F08B2F49A7CC6AF335E08C4720E31476B67299E231F8BD90B39AC3AE3BE0C6B6CACEF8289A2E2873D58E51E029CAFBD55E6841489AB66B5B4B9BA6E2F784660896AFF387D92844CCB8B69475496DE19DA2E58259B090489AC8E62363CDF82CFD8EF2A427ABCD65750B506F56DDE3B988567A88126B914D7828E2B63A6D7ED0747EC59E0E0A23CE7D8A74C1D2C2A7AFB6A29799620F00E11C33787F7DED3B30E1A22D09F1FBDA1ABBBFBF25CAE05A13F812E34563F99410E73B",
        16);
  }

  public void testZPK() {
    BigInteger r = new BigInteger("10", 16);
    BigInteger x1 = new BigInteger("110", 16);
    BigInteger gx1 = g.modPow(x1, p);
    ZKP zkp_x1 = new ZKP(p, q, g, gx1, x1, "receiver", r);
    String expected_gx1 = "12d6294211d57a55e5401777b1c2a3c273aaebcea30a3b248ec6033be24fe8bbf016c79dbfde018d66df67d575a6e5c793c25ceffe5fcbbe66aab0b7a1f9a8269269925ce3fd064b19deabc49ab4adec214211b60a6c2ae276292691733f3854ac79b61a0a78539bf43febd3736ff2042def47b8e24524a0bc720842cbd790a00bbbec38a7c9d6b1e1975713495cd40368622b5499ec5770650966b2e4306aa0432893caebd4972638ca3b55102a2b8b2b9c92a5a0e9bceca88f328c90a5b1b9f09a087fe6b822285a593ff484666b9aeaa173928d30e8d2377e67ae01c82f18fca1001ef30447513e45009e9e1791605f26500694cac9c51e3d78ad44ee65ed2a873f4504096a926e39f176d1bc0ba118f5e52e0947b1fec5c24ecc12e0f69b9faa48bbb5aaef07490e0a15d1d654af03b2b030efe5ff539132cb099f1bd5d7452fd12559444b9b921065979dea0d691244eec1313c4e9684c5a136e0e6c588e22d48225372fd93578f1428d451d377d7bf36b4823b85b29b2da74932fc136d";
    String expected_zkp_x1_b = "c068e5a22c8757c21eba360eff68b3a9a8fdf802dba51e7ebb2bcf6fff7317a1";
    String expected_zkp_x1_gr = "635a3c76f45d5c783b466b2afc77e127d3553685f3c4ac31c04653b0b3373b2287095355bf11803fc735f3f5449635acdd86d35c92edfa5470ed009fe205c3ce20e6584689cb1e14fac1891a60d1617800fcf31423e00fe2795260cbeafeb3069387bd869430bfe7cc4acb33b6f4a39d87fdcc809d138103f183d495d5ddefc88934d237e667e05c34b2118f1526c7bb87d413ac1a71af7bd5423735d5f7378aa21793a46bc4750d40b344aaaac18667cedc6a0eccf1f6e06d7984ba0ac8c0f89c2b297c55d7ba250acaceb3d46c30bf0a8772949b46379a8213171284e6df3962d2ff1437a52be8fd0ae5a22bee0f6d70802d57698210a3c4c174b3e46c91c17a022fda406cc7d452ed53db03a0381f7efd5085eb57d3fdc0c6cd0322ab67b94a8de387dd3e9b0791780a53ac8b45dd9508e36b481d8f3516d0e77f5d11c312487a92b71fc148af1284377255ccc11b9626b6acb11d9d6cdb23671f88afbfe400f14356f74336ca35888eb2be7b7553f30070f3bf2f541ecc9ec49656b43fcd";
    assertTrue(ZKP.verify(p, q, g, gx1, zkp_x1, "receiver"));
    assertEquals(gx1.toString(16), expected_gx1);
    assertEquals(zkp_x1.b.toString(16), expected_zkp_x1_b);
    assertEquals(zkp_x1.gr.toString(16), expected_zkp_x1_gr);

    BigInteger x2 = new BigInteger("111", 16);
    BigInteger gx2 = g.modPow(x2, p);
    ZKP zkp_x2 = new ZKP(p, q, g, gx2, x2, "receiver", r);
    String expected_gx2 = "48bad81b1f70401199ddac24ac2b7f3b5a8dcf97ad415dfa1a7a9678d55351d1bdd5215007880649e306df72faaa6051777932aa8e3bf12fbe44d4b8c739f5d15ce9e7ca117885b23ed6a1880f08f524933f6b8b365522a95b0d8b4aee911229109965d6abd27e1a80d03ddb082e2b7eef25965a6b10d1f8371def24a4222250aff2cefae6e869a9e3d92be7ad1a5e8047e43a21b0eb5dacb9b8a977d4c1c0ab5447f9a7ab5e4caed71bc965ebc98b9e7f6289e3a517da2309e0b58c622458e67bf729179e83a5dfa2736e7dfd89f3cf5bcfca1926dc72b7f144f8ddd51d3272ebdb08939c75fe58ef720dea27b6c3a0ca1f9cdb5191deceac80c5b9c681b52709087db25acb21e0fa7752c2f3792a3aa52e4e1fdbc4e81f21f936db7fdb44b0f64cce151b5e03462fad381ed82a0096ffcdcf847e55d9f3efac04ddda2dea121952d56b274e9715ef93036217a88df96d913ed06df537a95dc75f4db3fcb835775c06a472242b28eef5f167391e6a0195fdbea12763ebfd2c90189017875e32";
    String expected_zkp_x2_b = "954fcb01c46fb7cd07cb486f37ed594cc2ee5e9c097f19438c6525c3afd3180f";
    String expected_zkp_x2_gr = "635a3c76f45d5c783b466b2afc77e127d3553685f3c4ac31c04653b0b3373b2287095355bf11803fc735f3f5449635acdd86d35c92edfa5470ed009fe205c3ce20e6584689cb1e14fac1891a60d1617800fcf31423e00fe2795260cbeafeb3069387bd869430bfe7cc4acb33b6f4a39d87fdcc809d138103f183d495d5ddefc88934d237e667e05c34b2118f1526c7bb87d413ac1a71af7bd5423735d5f7378aa21793a46bc4750d40b344aaaac18667cedc6a0eccf1f6e06d7984ba0ac8c0f89c2b297c55d7ba250acaceb3d46c30bf0a8772949b46379a8213171284e6df3962d2ff1437a52be8fd0ae5a22bee0f6d70802d57698210a3c4c174b3e46c91c17a022fda406cc7d452ed53db03a0381f7efd5085eb57d3fdc0c6cd0322ab67b94a8de387dd3e9b0791780a53ac8b45dd9508e36b481d8f3516d0e77f5d11c312487a92b71fc148af1284377255ccc11b9626b6acb11d9d6cdb23671f88afbfe400f14356f74336ca35888eb2be7b7553f30070f3bf2f541ecc9ec49656b43fcd";
    assertTrue(ZKP.verify(p, q, g, gx2, zkp_x2, "receiver"));
    assertEquals(gx2.toString(16), expected_gx2);
    assertEquals(zkp_x2.b.toString(16), expected_zkp_x2_b);
    assertEquals(zkp_x2.gr.toString(16), expected_zkp_x2_gr);
  }

  public void testSHA256() {
    MessageDigest sha = null;
    byte[] result = null;
    String expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    try {
      sha = MessageDigest.getInstance("SHA-256");
      sha.reset();
      /*
       * Note: you should ensure the items in H(...) have clear boundaries. It
       * is simple if the other party knows sizes of g, gr, gx and signerID and
       * hence the boundary is unambiguous. If not, you'd better prepend each
       * item with its byte length, but I've omitted that here.
       */
      result = sha.digest(new byte[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertEquals(bytes2Hex(result), expected);
  }

  public void testSHA256ABC() {
    MessageDigest sha = null;
    byte[] result = null;
    try {
      sha = MessageDigest.getInstance("SHA-256");
      sha.reset();
      sha.update("abc".getBytes());

      /*
       * Note: you should ensure the items in H(...) have clear boundaries. It
       * is simple if the other party knows sizes of g, gr, gx and signerID and
       * hence the boundary is unambiguous. If not, you'd better prepend each
       * item with its byte length, but I've omitted that here.
       */
      result = sha.digest();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertEquals(result[0], (byte) 186);
    assertEquals(result[30], (byte) 21);
  }

  private static String bytes2Hex(byte[] bts) {
    String des = "";
    String tmp = null;
    for (int i = 0; i < bts.length; i++) {
      tmp = (Integer.toHexString(bts[i] & 0xFF));
      if (tmp.length() == 1) {
        des += "0";
      }
      des += tmp;
    }
    return des;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
