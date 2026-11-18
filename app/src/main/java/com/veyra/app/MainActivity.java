package com.veyra.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.provider.Settings;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.net.*;

public class MainActivity extends Activity {
 final int BG=0xff08090B, PANEL=0xff15171B, LINE=0xff2A2D32, WHITE=0xffF5F6F8, MUTED=0xff9298A2, ACCENT=0xffC8D5F0;
 LinearLayout page; TextView status; TextView version;
 int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
 GradientDrawable shape(int color,int radius,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));if(stroke!=0)d.setStroke(dp(1),stroke);return d;}
 TextView txt(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,1);return t;}
 void space(LinearLayout l,int h){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
 LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
 void add(LinearLayout parent,View child){parent.addView(child,new LinearLayout.LayoutParams(-1,-2));}
 LinearLayout card(){LinearLayout c=column();c.setPadding(dp(20),dp(20),dp(20),dp(20));c.setBackground(shape(PANEL,22,LINE));return c;}
 TextView action(String s,boolean primary){TextView b=txt(s,15,primary?BG:WHITE,true);b.setGravity(Gravity.CENTER);b.setBackground(shape(primary?WHITE:0xff25282E,15,primary?0:LINE));b.setMinHeight(dp(54));return b;}
 @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);scroll.setVerticalScrollBarEnabled(false);
  page=column();page.setPadding(dp(22),dp(30),dp(22),dp(30));scroll.addView(page);setContentView(scroll);
  LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);TextView logo=txt("◈  VEYRA",21,WHITE,true);logo.setLetterSpacing(.16f);top.addView(logo,new LinearLayout.LayoutParams(0,-2,1));
  TextView tag=txt("EARLY ACCESS",10,ACCENT,true);tag.setPadding(dp(11),dp(8),dp(11),dp(8));tag.setBackground(shape(0xff1C2533,20,0xff354358));top.addView(tag);add(page,top);
  space(page,39);add(page,txt("Mais controle.\nMenos distração.",32,WHITE,true));space(page,11);add(page,txt("Uma experiência discreta para sua tela.",15,MUTED,false));space(page,30);
  LinearLayout hero=card();TextView eyebrow=txt("PAINEL FLUTUANTE",11,ACCENT,true);eyebrow.setLetterSpacing(.15f);add(hero,eyebrow);space(hero,18);
  status=txt("●  Desativado",22,WHITE,true);add(hero,status);space(hero,9);add(hero,txt("Acesse seus controles sem sair do aplicativo que estiver usando.",14,MUTED,false));space(hero,24);
  TextView start=action("Ativar painel flutuante    ↗",true);add(hero,start);start.setOnClickListener(v->activate());
  space(hero,10);TextView stop=action("Desativar painel",false);add(hero,stop);stop.setOnClickListener(v->{stopService(new Intent(this,BubbleService.class));status.setText("●  Desativado");});add(page,hero);
  space(page,20);
  LinearLayout info=card();add(info,txt("CONTROLES",11,ACCENT,true));space(info,14);
  add(info,txt("Uma bolha. Zero distrações.",20,WHITE,true));space(info,9);
  add(info,txt("Arraste para qualquer posição, toque para expandir e deixe a bolha quase invisível quando estiver inativa.",14,MUTED,false));
  space(info,15);add(info,txt("Nesta prévia, os filtros ainda não modificam vídeos externos.",12,0xffE0B88B,false));add(page,info);
  space(page,20);
  LinearLayout updates=card();add(updates,txt("ATUALIZAÇÕES",11,ACCENT,true));space(updates,12);
  version=txt("Versão instalada: "+installedVersion(),15,WHITE,true);add(updates,version);space(updates,15);
  TextView check=action("Verificar nova versão    ↗",false);add(updates,check);check.setOnClickListener(v->checkUpdates(true));add(page,updates);
  space(page,28);add(page,txt("VEYRA  /  PREVIEW BUILD",11,MUTED,false));
  checkUpdates(false);
 }
 String installedVersion(){try{return getPackageManager().getPackageInfo(getPackageName(),0).versionName;}catch(Exception e){return "0.1.0";}}
 void activate(){if(!Settings.canDrawOverlays(this)){startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName())));return;}startService(new Intent(this,BubbleService.class));status.setText("●  Ativado");}
 @Override protected void onResume(){super.onResume();if(status!=null&&Settings.canDrawOverlays(this))status.setText("●  Permissão concedida");}
 void checkUpdates(boolean manual){new Thread(()->{try{
  HttpURLConnection c=(HttpURLConnection)new URL("https://api.github.com/repos/raphaelsancho21-byte/Veyra/releases/latest").openConnection();
  c.setConnectTimeout(7000);c.setReadTimeout(7000);c.setRequestProperty("Accept","application/vnd.github+json");
  if(c.getResponseCode()!=200){c.disconnect();if(manual)runOnUiThread(()->message("Não foi possível consultar as versões agora."));return;}
  java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();try(java.io.InputStream in=c.getInputStream()){byte[] buf=new byte[4096];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);}c.disconnect();
  JSONObject j=new JSONObject(out.toString("UTF-8"));String tag=j.optString("tag_name","").replaceFirst("^v","");
  String link=j.optString("html_url","");JSONArray assets=j.optJSONArray("assets");
  if(assets!=null)for(int i=0;i<assets.length();i++){JSONObject a=assets.optJSONObject(i);if(a!=null&&a.optString("name").endsWith(".apk")){link=a.optString("browser_download_url",link);break;}}
  final String download=link;
  runOnUiThread(()->{if(newer(tag,installedVersion())&&download.startsWith("https://github.com/"))new AlertDialog.Builder(this).setTitle("Nova versão disponível").setMessage("Veyra "+tag+" já pode ser baixado.").setNegativeButton("Depois",null).setPositiveButton("Baixar",(d,w)->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(download)))).show();else if(manual)message("Você já está na versão mais recente.");});
 }catch(Exception e){if(manual)runOnUiThread(()->message("Falha de conexão ao verificar atualizações."));}}).start();}
 void message(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
 boolean newer(String a,String b){try{String[] x=a.split("\\."),y=b.split("\\.");for(int i=0;i<3;i++){int u=i<x.length?Integer.parseInt(x[i]):0,v=i<y.length?Integer.parseInt(y[i]):0;if(u!=v)return u>v;}}catch(Exception ignored){}return false;}
}
