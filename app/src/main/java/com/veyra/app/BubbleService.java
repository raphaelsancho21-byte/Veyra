package com.veyra.app;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class BubbleService extends Service {
 WindowManager wm;LinearLayout panel;WindowManager.LayoutParams params;Handler handler=new Handler(Looper.getMainLooper());Runnable idle;
 boolean expanded=false;
 int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));d.setStroke(dp(1),0xff363A43);return d;}
 TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
 @Override public void onCreate(){super.onCreate();if(!Settings.canDrawOverlays(this)){stopSelf();return;}
  wm=(WindowManager)getSystemService(WINDOW_SERVICE);panel=new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);
  params=new WindowManager.LayoutParams(dp(56),dp(56),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,android.graphics.PixelFormat.TRANSLUCENT);
  params.gravity=Gravity.TOP|Gravity.START;params.x=dp(10);params.y=dp(210);render();wm.addView(panel,params);scheduleFade();
 }
 void scheduleFade(){handler.removeCallbacksAndMessages(null);panel.animate().alpha(1f).setDuration(160).start();if(!expanded){idle=()->panel.animate().alpha(.28f).setDuration(400).start();handler.postDelayed(idle,2800);}}
 void render(){panel.removeAllViews();panel.setBackground(shape(0xff15171B,expanded?23:28));panel.setPadding(expanded?dp(18):0,expanded?dp(16):0,expanded?dp(18):0,expanded?dp(15):0);
  if(!expanded){TextView mark=text("◈",27,0xffE9EDF7);mark.setGravity(Gravity.CENTER);panel.addView(mark,new LinearLayout.LayoutParams(-1,-1));mark.setOnTouchListener(new View.OnTouchListener(){float x,y;int px,py;boolean dragged;
   @Override public boolean onTouch(View v,MotionEvent e){switch(e.getActionMasked()){
    case MotionEvent.ACTION_DOWN:x=e.getRawX();y=e.getRawY();px=params.x;py=params.y;dragged=false;scheduleFade();return true;
    case MotionEvent.ACTION_MOVE:float dx=e.getRawX()-x,dy=e.getRawY()-y;if(Math.abs(dx)>dp(7)||Math.abs(dy)>dp(7))dragged=true;params.x=px+(int)dx;params.y=py+(int)dy;wm.updateViewLayout(panel,params);return true;
    case MotionEvent.ACTION_UP:if(!dragged)toggle();else scheduleFade();return true;
   }return false;}});return;}
  LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);
  TextView name=text("◈   VEYRA",17,0xffF4F5F7);name.setTypeface(null,1);header.addView(name,new LinearLayout.LayoutParams(0,-2,1));
  TextView collapse=text("—",22,0xffB8BFCC);collapse.setPadding(dp(12),0,dp(4),0);header.addView(collapse);collapse.setOnClickListener(v->toggle());panel.addView(header);
  TextView subtitle=text("PAINEL FLUTUANTE",10,0xff9DAAC0);subtitle.setPadding(0,dp(10),0,dp(14));panel.addView(subtitle);
  TextView warning=text("Prévia de interface • sem processamento de vídeo",12,0xffE2C39D);panel.addView(warning);
  TextView close=text("Encerrar painel",13,0xffF1F1F1);close.setGravity(Gravity.CENTER);close.setBackground(shape(0xff282B31,12));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(45));lp.topMargin=dp(18);panel.addView(close,lp);close.setOnClickListener(v->stopSelf());
 }
 void toggle(){expanded=!expanded;params.width=dp(expanded?300:56);params.height=expanded?-2:dp(56);render();wm.updateViewLayout(panel,params);scheduleFade();}
 @Override public void onDestroy(){handler.removeCallbacksAndMessages(null);if(wm!=null&&panel!=null)try{wm.removeView(panel);}catch(Exception ignored){}super.onDestroy();}
 @Override public IBinder onBind(Intent intent){return null;}
}
