package com.veyra.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
 LinearLayout root;
 int white=Color.rgb(241,241,241), muted=Color.rgb(150,150,150);
 GradientDrawable bg(int color,int radius) { GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d; }
 TextView text(String s,int size,int color) { TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t; }
 void addGap(int h) { View v=new View(this);root.addView(v,new LinearLayout.LayoutParams(1,h)); }
 Button button(String title) { Button b=new Button(this);b.setText(title);b.setTextColor(white);b.setAllCaps(false);b.setBackground(bg(0xff242424,20));root.addView(b,new LinearLayout.LayoutParams(-1,56));return b; }
 @Override public void onCreate(Bundle b) {super.onCreate(b);
  root=new LinearLayout(this);root.setOrientation(1);root.setPadding(30,60,30,28);root.setBackgroundColor(0xff090909);
  ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
  TextView title=text("VEYRA",32,white);title.setTypeface(null,Typeface.BOLD);root.addView(title);
  root.addView(text("DISPLAY COMPANION  •  0.1.0",12,muted));addGap(38);
  root.addView(text("A imagem, sob controle.",23,white));addGap(12);
  root.addView(text("Interface flutuante minimalista para controles de imagem. Nesta versão, os ajustes são demonstrativos: não modificam vídeos de outros aplicativos.",15,muted));addGap(30);
  Button bubble=button("Ativar bolha flutuante");bubble.setOnClickListener(v->{
   if(!Settings.canDrawOverlays(this)) {Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));startActivity(i);}
   else {startService(new Intent(this,BubbleService.class));Toast.makeText(this,"Bolha ativada",Toast.LENGTH_SHORT).show();}
  });
  addGap(12);Button stop=button("Ocultar bolha");stop.setOnClickListener(v->stopService(new Intent(this,BubbleService.class)));
  addGap(12);Button update=button("Verificar atualizações");update.setOnClickListener(v->checkUpdates());
  addGap(30);root.addView(text("Privacidade",18,white));addGap(8);
  root.addView(text("Sem captura de tela, sem gravação e sem acesso aos vídeos. O Veyra não contorna proteção DRM.",14,muted));
  checkUpdates();
 }
 void checkUpdates() {
  new Thread(()->{
   try {
    HttpURLConnection c=(HttpURLConnection)new URL("https://api.github.com/repos/raphaelsancho21-byte/Veyra/releases/latest").openConnection();
    c.setConnectTimeout(7000);c.setReadTimeout(7000);c.setRequestProperty("Accept","application/vnd.github+json");
    if(c.getResponseCode()!=200) {c.disconnect();return;}
    java.io.InputStream in=c.getInputStream();java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();byte[] buf=new byte[4096];int n;
    while((n=in.read(buf))!=-1)out.write(buf,0,n);in.close();c.disconnect();
    JSONObject json=new JSONObject(out.toString("UTF-8"));
    String tag=json.optString("tag_name","").replaceFirst("^v","");
    String url=json.optString("html_url","");
    org.json.JSONArray assets=json.optJSONArray("assets");
    if(assets!=null)for(int i=0;i<assets.length();i++){JSONObject a=assets.optJSONObject(i);if(a!=null&&"veyra.apk".equals(a.optString("name"))){url=a.optString("browser_download_url",url);break;}}
    final String downloadUrl=url;
    String installed=getPackageManager().getPackageInfo(getPackageName(),0).versionName;
    if(newer(tag,installed) && downloadUrl.startsWith("https://github.com/"))runOnUiThread(()->new android.app.AlertDialog.Builder(this)
     .setTitle("Atualização disponível").setMessage("Veyra "+tag+" está disponível. Abrir página para baixar?")
     .setNegativeButton("Depois",null).setPositiveButton("Baixar",(d,w)->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(downloadUrl)))).show());
   }catch(Exception ignored){}
  }).start();
 }
 boolean newer(String a,String b) {
  try {String[] x=a.split("\\."),y=b.split("\\.");for(int i=0;i<3;i++){int u=i<x.length?Integer.parseInt(x[i]):0,v=i<y.length?Integer.parseInt(y[i]):0;if(u!=v)return u>v;} }catch(Exception ignored){}return false;
 }
}
