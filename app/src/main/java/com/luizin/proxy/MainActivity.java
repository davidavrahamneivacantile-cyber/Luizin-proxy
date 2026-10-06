package com.luizin.proxy;
import android.app.Activity;
import android.os.Bundle;
import android.content.*;
import android.net.Uri;
import android.webkit.*;
import android.widget.Toast;

public class MainActivity extends Activity {
 private WebView web;
 private static final String LOCAL="https://appassets.androidplatform.net/index.html";
 @Override public void onCreate(Bundle state){
  super.onCreate(state);
  web=new WebView(this); setContentView(web);
  WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
  s.setAllowFileAccess(false); s.setAllowContentAccess(false);
  s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  web.setWebChromeClient(new WebChromeClient());
  web.addJavascriptInterface(new Bridge(),"LuizinAndroid");
  web.setWebViewClient(new WebViewClient(){
   @Override public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){
    if(LOCAL.equals(r.getUrl().toString())) {
     try{return new WebResourceResponse("text/html","UTF-8",getAssets().open("index.html"));}
     catch(Exception e){return new WebResourceResponse("text/plain","UTF-8",404,"Not Found",null,new java.io.ByteArrayInputStream(new byte[0]));}
    }
    return new WebResourceResponse("text/plain","UTF-8",403,"Forbidden",null,new java.io.ByteArrayInputStream(new byte[0]));
   }
   @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return navigate(r.getUrl().toString());}
   @Override public boolean shouldOverrideUrlLoading(WebView v,String url){return navigate(url);}
  });
  web.loadUrl(LOCAL);
 }
 private boolean navigate(String url){
  if(LOCAL.equals(url))return false;
  if(url.startsWith("intent:")){openGame();return true;}
  if(url.startsWith("https://wa.me/")){
   try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
   catch(ActivityNotFoundException e){message("Não foi possível abrir o WhatsApp ou navegador.");}
  }
  return true;
 }
 private void message(String text){Toast.makeText(this,text,Toast.LENGTH_LONG).show();}
 private void openGame(){
  for(String pkg:new String[]{"com.dts.freefireth","com.dts.freefiremax"}){
   Intent intent=getPackageManager().getLaunchIntentForPackage(pkg);
   if(intent!=null){try{startActivity(intent);return;}catch(ActivityNotFoundException e){}}
  }
  message("Free Fire não encontrado. Instale o jogo para abri-lo.");
 }
 private class Bridge {
  @JavascriptInterface public void copyText(String text){runOnUiThread(()->{
   ClipboardManager cm=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
   cm.setPrimaryClip(ClipData.newPlainText("Key Luizin Proxy",text));message("Key copiada!");
  });}
  @JavascriptInterface public void openFreeFire(){runOnUiThread(()->openGame());}
 }
 @Override public void onBackPressed(){web.evaluateJavascript("show('login')",null);}
 @Override protected void onDestroy(){web.removeJavascriptInterface("LuizinAndroid");web.destroy();super.onDestroy();}
}
