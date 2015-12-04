package net.thepaca.hunydoo;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.util.Log;

/**
 * 
 */

/**
 * 5/24	AR	Helper to check if the system can support the given "intent"
 *
 */
public class HunyDewUtils {
	
	// Define the debug signature hash (Android default debug cert). Code from sigs[i].hashCode()
	//protected final static int DEBUG_SIGNATURE_HASH = <your hash value>;
	//static final String DEBUGKEY = "308201e53082014ea00302010202044bf236b2300d06092a864886f70d01010505003037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f6964204465627567301e170d3130303531383036343135345a170d3131303531383036343135345a3037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f696420446562756730819f300d06092a864886f70d010101050003818d00308189028181008ca181bb0b4dd7088c75b4325a878f8c0eb974d3810ce587d8e6232e4274d6d171342a17fea0bf2ac1e9e025a68844a3c70c93361185b14def5ee0d9d41791a97fbc75dfd8d0bd6f02ab87114efb15b386ee365cff156545dcdd7bc3fe365e44dd91c4c493a15d66dcd84861fed14d768b9e0d78f2268a723c3cdca5f4ee3f570203010001300d06092a864886f70d010105050003818100151ecd3b45aede985175171440db9fb8340216e3e1e14e3804cb11511a551bb695937f206cbfbdbfb332910e506bf37e38e67e25a05356fcf2112241db7c6c663b545ca5a01090598ca4bc240e1958c6c6286b3a8b112fbbfbf27e97a39bd294e0408bb8ed168b3c1565b2f5bc5e599bd024ed099f910095be8f20a8276fb0cd";
	//static final String DEBUGKEY = "308201e53082014ea00302010202044db315fc300d06092a864886f70d01010505003037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f6964204465627567301e170d3131303432333138313030345a170d3132303432323138313030345a3037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f696420446562756730819f300d06092a864886f70d010101050003818d0030818902818100804df1ac8822f8cf4e935bab2bfa0fc433efaea146e23412869f49ff622bd478335e45100c04c7d2536061f6cba40228de05af22947cce19afa165e131e19ca2415b3edaa3c42938d9be4f13c325354a32a387f8239356c36d6204073434fdfe4d8082ac3cc19403e622eeb3d176139e93e61540780359be9dece7a2b75183fd0203010001300d06092a864886f70d0101050500038181005e6cfbed7dd92170a327df8b06bc4efdeeae2c27ac10cfa12b25648d639f4de179c039dc04de8105baaaad52954aebbcfbe7d79f17ddc77537bd79db3b4f3317282fc2ac83ff106b995d30196bce48fcd079340f30d4192d304404603c77fed4bc2f5007fe8476f35d23938e869564346992e27fe5c804dac43c07fd4011012c";
	// this DEBUGKEY renewd on May 24, 2011 (using new debug.store)
	static final String DEBUGKEY = "308201e53082014ea00302010202044ddafb93300d06092a864886f70d01010505003037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f6964204465627567301e170d3131303532343030323830335a170d3132303532333030323830335a3037310b30090603550406130255533110300e060355040a1307416e64726f6964311630140603550403130d416e64726f696420446562756730819f300d06092a864886f70d010101050003818d0030818902818100b6e250e57704b66c01d7f446d80836ce2c2af718cc0afccd07583617b15e96cf514c90df07c81dfb8634c271130ee647ace285b5b76db2338364a8d54c79abe108fadb9ee4bcf4d017c35d3d8c753fbd452c90f59826d82827a932269dac6022feec6ba406b7ab080a1365fa40c672a1aeb286e7e0a6bde8ca1d17d0558f48710203010001300d06092a864886f70d0101050500038181006ce86791e7a90a1128379b0aea0dd1783c32f5dbc68fc56dd76ed990707cfa8cf3a2750e75f7304268f6323b0529f8003352dcef8ecb83947ce11db63ff52bf296c2b76fc868fa639f947204dd78b441d698ede49205bd28e0e2ae42d1bb63fce006a59ee33be0f3b587fd9935b3cf2b849e4d590c57ff780054dfd3162c974a";


	
	public static boolean signedWithDebugKey(Context context, Class<?> cls) 
	{
	    boolean result = false;
	    try {
	        ComponentName comp = new ComponentName(context, cls);
	        PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(),PackageManager.GET_SIGNATURES);
	        Signature sigs[] = pinfo.signatures;
	        for ( int i = 0; i < sigs.length;i++)
	        	Log.d("Utils",sigs[i].toCharsString());
	        if (DEBUGKEY.equals(sigs[0].toCharsString())) {
	            result = true;
	            Log.d("Utils","package has been signed with the debug key");
	        } else {
	            Log.d("Utils","package signed with a key other than the debug key");
	        }

	    } catch (android.content.pm.PackageManager.NameNotFoundException e) {
	        return false;
	    }

	    return result;

	} 
	
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent,
	                    PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
}
