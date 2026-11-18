package com.veyra.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class BubbleService extends Service {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private WindowManager manager;
 private WindowManager.LayoutParams params;
 private LinearLayout root;
 private boolean expanded=false;
 private Runnable fadeTask;
 private final int BLACK=0xff101115, SURFACE=0xff1D1F25, WHITE=0xffF5F6F9, MUTED=0xff9B9FAA, ACCENT=0xffB9C9ED;
 private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
 private GradientDrawable bg(int color,int radius,int border){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));if(border!=0)d.setStroke(dp(1),border);return d;}
 private TextView label(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,1);return t;}
 private void gap(int h){View v=new View(this);root.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
 @Override public void onCreate(){super.onCreate();if(!Settings.canDrawOverlays(this)){stopSelf();return;}
  manager=(WindowManager)getSystemService(WINDOW_SERVICE);
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  params=new WindowManager.LayoutParams(dp(54),dp(54),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
   WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
  params.gravity=Gravity.TOP|Gravity.START;params.x=dp(12);params.y=dp(210);
  draw();manager.addView(root,params);fadeLater();
 }
 private void fadeLater(){handler.removeCallbacksAndMessages(null);root.animate().alpha(1f).setDuration(180).start();
  if(!expanded){fadeTask=()->root.animate().alpha(.22f).setDuration(500).start();handler.postDelayed(fadeTask,2600);}
 }
 private void draw(){
  root.removeAllViews();root.setPadding(0,0,0,0);
  root.setBackground(bg(BLACK,expanded?24:27,0xff424650));
  if(!expanded){
   TextView logo=label("V",23,WHITE,true);logo.setGravity(Gravity.CENTER);logo.setLetterSpacing(.02f);
   root.addView(logo,new LinearLayout.LayoutParams(-1,-1));
   logo.setOnTouchListener(new View.OnTouchListener(){float downX,downY;int originX,originY;boolean moved;
    @Override public boolean onTouch(View v,MotionEvent e){switch(e.getActionMasked()){
     case MotionEvent.ACTION_DOWN:downX=e.getRawX();downY=e.getRawY();originX=params.x;originY=params.y;moved=false;fadeLater();return true;
     case MotionEvent.ACTION_MOVE:float dx=e.getRawX()-downX,dy=e.getRawY()-downY;
      if(Math.abs(dx)>dp(8)||Math.abs(dy)>dp(8))moved=true;
      params.x=originX+(int)dx;params.y=originY+(int)dy;manager.updateViewLayout(root,params);return true;
     case MotionEvent.ACTION_UP:if(!moved)toggle();else fadeLater();return true;
    }return false;}});
   return;
  }
  root.setPadding(dp(18),dp(17),dp(18),dp(17));
  LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);
  TextView brand=label("V E Y R A",16,WHITE,true);head.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
  TextView minimize=label("⌄",25,MUTED,false);minimize.setGravity(Gravity.CENTER);
  head.addView(minimize,new LinearLayout.LayoutParams(dp(40),dp(38)));minimize.setOnClickListener(v->toggle());root.addView(head);
  gap(4);TextView sub=label("CONTROLE DE EXIBIÇÃO",10,ACCENT,true);sub.setLetterSpacing(.13f);root.addView(sub);
  gap(18);
  LinearLayout state=new LinearLayout(this);state.setOrientation(LinearLayout.VERTICAL);state.setPadding(dp(15),dp(15),dp(15),dp(15));state.setBackground(bg(SURFACE,16,0xff31343D));
  TextView stateTitle=label("●  Modo de visualização",14,WHITE,true);state.addView(stateTitle);
  TextView desc=label("Painel ativo. Nenhum filtro aplicado ao vídeo.",12,MUTED,false);
  LinearLayout.LayoutParams dlp=new LinearLayout.LayoutParams(-1,-2);dlp.topMargin=dp(7);state.addView(desc,dlp);root.addView(state);
  gap(16);
  TextView help=label("A melhoria de imagem do Seekee ainda não está disponível. O Android não permite alterar diretamente os quadros de outros aplicativos.",12,MUTED,false);
  help.setLineSpacing(dp(3),1f);root.addView(help);
  gap(18);
  TextView open=label("Abrir configurações  ↗",13,BLACK,true);open.setGravity(Gravity.CENTER);open.setBackground(bg(WHITE,13,0));
  root.addView(open,new LinearLayout.LayoutParams(-1,dp(46)));
  open.setOnClickListener(v->{Intent i=new Intent(this,MainActivity.class);i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);toggle();});
  gap(10);
  TextView close=label("Desativar painel",13,MUTED,false);close.setGravity(Gravity.CENTER);
  root.addView(close,new LinearLayout.LayoutParams(-1,dp(35)));close.setOnClickListener(v->stopSelf());
 }
 private void toggle(){expanded=!expanded;params.width=dp(expanded?310:54);params.height=expanded?WindowManager.LayoutParams.WRAP_CONTENT:dp(54);
  draw();manager.updateViewLayout(root,params);fadeLater();}
 @Override public void onDestroy(){handler.removeCallbacksAndMessages(null);if(manager!=null&&root!=null)try{manager.removeView(root);}catch(Exception ignored){}super.onDestroy();}
 @Override public IBinder onBind(Intent intent){return null;}
}
