package com.veyra.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class BubbleService extends Service {
 WindowManager wm;LinearLayout bubble;WindowManager.LayoutParams lp;Handler handler=new Handler(Looper.getMainLooper());
 boolean expanded=false;
 GradientDrawable bg(int c,int r){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(r);d.setStroke(1,0xff555555);return d;}
 @Override public void onCreate(){super.onCreate();if(!Settings.canDrawOverlays(this)){stopSelf();return;}
  wm=(WindowManager)getSystemService(WINDOW_SERVICE);
  bubble=new LinearLayout(this);bubble.setGravity(Gravity.CENTER);bubble.setOrientation(LinearLayout.VERTICAL);bubble.setBackground(bg(0xff171717,32));
  lp=new WindowManager.LayoutParams(56,56,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
  lp.gravity=Gravity.TOP|Gravity.START;lp.x=0;lp.y=220;
  render();wm.addView(bubble,lp);fade();
 }
 void fade(){handler.removeCallbacksAndMessages(null);bubble.animate().alpha(1f).setDuration(150).start();handler.postDelayed(()->{if(!expanded)bubble.animate().alpha(.25f).setDuration(450).start();},3000);}
 TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setPadding(12,10,12,10);return t;}
 void render(){
  bubble.removeAllViews();if(!expanded){
   TextView icon=label("V",21);icon.setGravity(Gravity.CENTER);bubble.addView(icon);
   icon.setOnClickListener(v->toggle());
   icon.setOnTouchListener(new View.OnTouchListener(){float downX,downY;int startX,startY;boolean moved;
    public boolean onTouch(View v,android.view.MotionEvent e){
     switch(e.getAction()){
      case MotionEvent.ACTION_DOWN:downX=e.getRawX();downY=e.getRawY();startX=lp.x;startY=lp.y;moved=false;fade();return true;
      case MotionEvent.ACTION_MOVE:float dx=e.getRawX()-downX,dy=e.getRawY()-downY;if(Math.abs(dx)>8||Math.abs(dy)>8)moved=true;lp.x=startX+(int)dx;lp.y=startY+(int)dy;wm.updateViewLayout(bubble,lp);return true;
      case MotionEvent.ACTION_UP:if(!moved)toggle();else fade();return true;
     }return false;
    }
   });
  }else{
   TextView title=label("VEYRA    —",18);bubble.addView(title);title.setOnClickListener(v->toggle());
   bubble.addView(label("Controles demonstrativos",12));
   for(String name:new String[]{"Nitidez","Redução de blocos","Contraste"}){
    bubble.addView(label(name,13));SeekBar bar=new SeekBar(this);bar.setMax(100);bar.setProgress(30);bubble.addView(bar);
   }
   TextView note=label("Vídeo externo não é processado",11);bubble.addView(note);
   TextView close=label("Fechar bolha",13);bubble.addView(close);close.setOnClickListener(v->stopSelf());
  }
 }
 void toggle(){expanded=!expanded;lp.width=expanded?300:56;lp.height=expanded?WindowManager.LayoutParams.WRAP_CONTENT:56;render();wm.updateViewLayout(bubble,lp);fade();}
 @Override public void onDestroy(){handler.removeCallbacksAndMessages(null);if(wm!=null&&bubble!=null){try{wm.removeView(bubble);}catch(Exception ignored){}}super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}
