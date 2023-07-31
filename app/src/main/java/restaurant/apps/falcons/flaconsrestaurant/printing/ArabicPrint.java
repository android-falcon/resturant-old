package restaurant.apps.falcons.flaconsrestaurant.printing;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArabicPrint {
    private static int[][] a = new int[][]{{'ﺀ', 193}, {'ﺁ', 194}, {'ﺂ', 162}, {'ﺃ', 195}, {'ﺄ', 165}, {'ﺅ', 196},
            {'ﺆ', 196}, {'ﺇ', 199}, {'ﺈ', 168}, {'ﺉ', 233}, {'ﺊ', 245}, {'ﺋ', 198}, {'ﺌ', 198}, {'ﺍ', 199}, {'ﺎ', 168},
            {'ﺏ', 169}, {'ﺐ', 169}, {'ﺑ', 200}, {'ﺒ', 200}, {'ﺓ', 201}, {'ﺔ', 254}, {'ﺕ', 170}, {'ﺖ', 170}, {'ﺗ', 202},
            {'ﺘ', 202}, {'ﺙ', 171}, {'ﺚ', 171}, {'ﺛ', 203}, {'ﺜ', 203}, {'ﺝ', 173}, {'ﺞ', 241}, {'ﺟ', 204}, {'ﺠ', 204},
            {'ﺡ', 174}, {'ﺢ', 240}, {'ﺣ', 205}, {'ﺤ', 205}, {'ﺥ', 175}, {'ﺦ', 192}, {'ﺧ', 206}, {'ﺨ', 206}, {'ﺩ', 207},
            {'ﺪ', 207}, {'ﺫ', 208}, {'ﺬ', 208}, {'ﺭ', 209}, {'ﺮ', 209}, {'ﺯ', 210}, {'ﺰ', 210}, {'ﺱ', 188}, {'ﺲ', 188},
            {'ﺳ', 211}, {'ﺴ', 211}, {'ﺵ', 189}, {'ﺶ', 189}, {'ﺷ', 212}, {'ﺸ', 212}, {'ﺹ', 190}, {'ﺺ', 190}, {'ﺻ', 213},
            {'ﺼ', 213}, {'ﺽ', 235}, {'ﺾ', 235}, {'ﺿ', 214}, {'ﻀ', 214}, {'ﻁ', 215}, {'ﻂ', 215}, {'ﻃ', 215}, {'ﻄ', 215},
            {'ﻅ', 216}, {'ﻆ', 216}, {'ﻇ', 216}, {'ﻈ', 216}, {'ﻉ', 223}, {'ﻊ', 197}, {'ﻋ', 217}, {'ﻌ', 236}, {'ﻍ', 238},
            {'ﻎ', 237}, {'ﻏ', 218}, {'ﻐ', 247}, {'ﻑ', 186}, {'ﻒ', 186}, {'ﻓ', 225}, {'ﻔ', 225}, {'ﻕ', 248}, {'ﻖ', 248},
            {'ﻗ', 226}, {'ﻘ', 226}, {'ﻙ', 252}, {'ﻚ', 252}, {'ﻛ', 227}, {'ﻜ', 227}, {'ﻝ', 251}, {'ﻞ', 251}, {'ﻟ', 228},
            {'ﻠ', 228}, {'ﻡ', 239}, {'ﻢ', 239}, {'ﻣ', 229}, {'ﻤ', 229}, {'ﻥ', 242}, {'ﻦ', 242}, {'ﻧ', 230}, {'ﻨ', 230},
            {'ﻩ', 243}, {'ﻪ', 220}, {'ﻫ', 231}, {'ﻬ', 244}, {'ﻭ', 232}, {'ﻮ', 232}, {'ﻯ', 233}, {'ﻰ', 245}, {'ﻱ', 253},
            {'ﻲ', 246}, {'ﻳ', 234}, {'ﻴ', 234}, {'ﻵ', 249}, {'ﻶ', 250}, {'ﻷ', 153}, {'ﻸ', 154}, {'ﻹ', 157}, {'ﻺ', 158},
            {'ﻻ', 166}, {'ﻼ', 156}};
    private Context b;
    private SimpleDateFormat c = new SimpleDateFormat("yyyy-MM-dd");
    private long d = 86400000L;
    private static String e = "sup3rS3xy";

    private static byte a(char var0) {
        char var1 = var0;
        int var2 = a.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            if (var1 == a[var3][0]) {
                return (byte) a[var3][1];
            }
        }

        return (byte) var0;
    }

    public byte[] Get_printerCode(String var1, Context var2, int var4) {
        String var5 = Secure.getString(var2.getContentResolver(), "android_id");
        this.b = var2;
        String[] var6 = var1.split("\n");
        String var8 = "";
        String var3 = "";
        if (var4 != 6) {
            var8 = var3;
        }

        Comiler var9 = new Comiler();

        for (var4 = 0; var4 < var6.length; ++var4) {
            var8 = var8 + var9.Compile(var6[var4]);
            var8 = var8 + "\r\n";
        }

        byte[] var10 = new byte[(var8 = var8 + "\r\n\r\n\r\n").length()];

        for (int var7 = 0; var7 < var10.length; ++var7) {
            var10[var7] = a(var8.charAt(var7));
            if((int)var8.charAt(var7)==1577){
                System.out.print("aaa");
            }
        }

        return var10;
    }

    public String convert_2_unicode(String var1) {
        String var2 = "";

        try {
            byte[] var4 = var1.getBytes("UTF-8");
            var2 = new String(var4, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            ;
        }

        return var2;
    }

    public int Get_Cash_info() {
        String var1;
        if ((var1 = this.b.getSharedPreferences("CheckedLicenceDate", 0).getString("Cashed", (String) null)) == null) {
            return 5;
        } else {
            SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd");
            Date var3 = null;

            try {
                var3 = var2.parse(var1);
            } catch (ParseException var10) {
                var10.printStackTrace();
            }

            return ((new Date()).getTime() - var3.getTime()) / this.d < 1L ? 6 : 5;
        }
    }

    public void Set_Cash_info() {
        SharedPreferences var1 = this.b.getSharedPreferences("CheckedLicenceDate", 0);
        String var2 = this.c.format(new Date());
        Editor var3;
        (var3 = var1.edit()).putString("Cashed", var2);
        var3.commit();
    }

    public static String encryptIt(String var0) {
        try {
            DESKeySpec var1 = new DESKeySpec(e.getBytes("UTF8"));
            SecretKey var11 = SecretKeyFactory.getInstance("DES").generateSecret(var1);
            byte[] var2 = var0.getBytes("UTF8");
            Cipher var3;
            (var3 = Cipher.getInstance("DES")).init(1, var11);
            return Base64.encodeToString(var3.doFinal(var2), 0);
        } catch (InvalidKeyException var4) {
            var4.printStackTrace();
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        } catch (InvalidKeySpecException var6) {
            var6.printStackTrace();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        } catch (BadPaddingException var8) {
            var8.printStackTrace();
        } catch (NoSuchPaddingException var9) {
            var9.printStackTrace();
        } catch (IllegalBlockSizeException var10) {
            var10.printStackTrace();
        }

        return var0;
    }

    public static String decryptIt(String var0) {
        try {
            DESKeySpec var1 = new DESKeySpec(e.getBytes("UTF8"));
            SecretKey var11 = SecretKeyFactory.getInstance("DES").generateSecret(var1);
            byte[] var2 = Base64.decode(var0, 0);
            Cipher var3;
            (var3 = Cipher.getInstance("DES")).init(2, var11);
            byte[] var12 = var3.doFinal(var2);
            return new String(var12);
        } catch (InvalidKeyException var4) {
            var4.printStackTrace();
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        } catch (InvalidKeySpecException var6) {
            var6.printStackTrace();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        } catch (BadPaddingException var8) {
            var8.printStackTrace();
        } catch (NoSuchPaddingException var9) {
            var9.printStackTrace();
        } catch (IllegalBlockSizeException var10) {
            var10.printStackTrace();
        }

        return var0;
    }
}
