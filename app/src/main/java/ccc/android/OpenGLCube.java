package ccc.android;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;  
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;  
import javax.microedition.khronos.opengles.GL10;  
import android.opengl.GLU;  
import android.os.SystemClock;  
import java.nio.ByteBuffer;  
import java.nio.ByteOrder;  
import java.nio.FloatBuffer;  
import java.nio.ShortBuffer;  

public class OpenGLCube extends Activity {
    
	  private GlView mGlView;  
	  
	  public void onCreate(Bundle savedInstanceState){  
	    super.onCreate(savedInstanceState);  
	    mGlView = new GlView(this);  
	    setContentView(mGlView);  
	  }  
}

class GlView extends GLSurfaceView{  
	  
	  private GlRender mGlRender;   
	  
	  public GlView(Context context){  
	    super(context);  
	      
	    mGlRender = new GlRender();  
	    setRenderer(mGlRender);  
	  }  
}  

class GlRender implements GLSurfaceView.Renderer{  
	  
	  private final static int VERTS = 8;
	  //頂點座標
	  float cubeVtx[] = { -1.0f,  1.0f, -1.0f,   //v0  
	                      -1.0f,  1.0f,  1.0f,   //v1  
	                       1.0f,  1.0f,  1.0f,   //v2  
	                       1.0f,  1.0f, -1.0f,   //v3  
	                      -1.0f, -1.0f, -1.0f,   //v4  
	                      -1.0f, -1.0f,  1.0f,   //v5  
	                       1.0f, -1.0f,  1.0f,   //v6  
	                       1.0f, -1.0f, -1.0f }; //v7  
	  //頂點索引
	  short cubeInx[] = { 0, 1, 2,   //Top_Face1  
	                      0, 2, 3,   //Top_Face2  
	                      4, 6, 5,   //Bottom_Face1  
	                      4, 7, 6,   //Bottom_Face2  
	                      2, 6, 7,   //Left_face1  
	                      2, 7, 3,   //Left_Face2  
	                      0, 4, 1,   //Right_Face1  
	                      1, 4, 5,   //Right_Face2  
	                      1, 5, 6,   //Front_Face1  
	                      1, 6, 2,   //Front_Face2  
	                      0, 3, 4,   //Back_Face1  
	                      3, 7, 4 }; //Back_Face2  
	   
	  @Override  
	  public void onSurfaceCreated(GL10 gl,EGLConfig config){  
	    gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);   
	    gl.glShadeModel(GL10.GL_SMOOTH);  
	    gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_FASTEST);   
	    gl.glClearDepthf(1.0f);  
	    gl.glDepthFunc(GL10.GL_LEQUAL);  
	    gl.glEnable(GL10.GL_DEPTH_TEST);    
	  }  
	   
	  @Override  
	  public void onSurfaceChanged(GL10 gl,int w,int h){  
	    gl.glViewport(0, 0, w, h);  
	    float ratio;  
	    ratio = (float)w/h;  
	    gl.glMatrixMode(GL10.GL_PROJECTION);  
	    gl.glLoadIdentity();   
	    gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);    
	  }  
	   
	  @Override  
	  public void onDrawFrame(GL10 gl){   
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   
	    gl.glMatrixMode(GL10.GL_MODELVIEW);  
	    gl.glLoadIdentity();   
	    GLU.gluLookAt(gl, 0, 0, 4, 0, 0, 0, 0, 1, 0);  
	    gl.glFrontFace(GL10.GL_CCW);    
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//設置光源
	    gl.glEnable(GL10.GL_LIGHT1);  
	    FloatBuffer lightAmbient = FloatBuffer.wrap(new float[]{0.2f,0.0f,0.0f,1.0f});  
	    FloatBuffer lightDiffuse = FloatBuffer.wrap(new float[]{0.8f,0.0f,0.0f,1.0f});  
	    FloatBuffer lightSpecular =  FloatBuffer.wrap(new float[]{1.0f,0.0f,0.0f,1.0f});  
	    FloatBuffer lightPosition = FloatBuffer.wrap(new float[]{3.0f,4.0f,10.0f,1.0f});  
	    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient);  
	    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse);  
	    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, lightSpecular);  
	    gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition);  
	    gl.glEnable(GL10.GL_LIGHTING);
		//動態旋轉
	    long time = SystemClock.uptimeMillis()% 4000L;  
	    float angle = 0.090f * ((int)time);  
	    gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);  
	    gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);  
	    gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
		//畫出方體
	    drawCube( gl, cubeVtx , cubeInx );     
	  }  
	   
	  private void drawCube(GL10 gl,float[] vtx, short[] inx){   
	    gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, getFloatBuffer(vtx));
		//透過索引緩衝來畫三角面
	    gl.glDrawElements(GL10.GL_TRIANGLES,  
	                      inx.length,  
	                      GL10.GL_UNSIGNED_SHORT,  
	                      getShortBuffer(inx));  
	  }

	 //頂點緩衝
	  private FloatBuffer getFloatBuffer(float[] array ){  
	    ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);  
	    bb.order(ByteOrder.nativeOrder());  
	    FloatBuffer fb = bb.asFloatBuffer();  
	    fb.put(array);  
	    fb.position(0);  
	    return fb;  
	  }

	  //索引緩衝
	  private ShortBuffer getShortBuffer(short[] array ){
	    ByteBuffer ib = ByteBuffer.allocateDirect(array.length * 2);  
	    ib.order(ByteOrder.nativeOrder());  
	    ShortBuffer sb = ib.asShortBuffer();  
	    sb.put(array);  
	    sb.position(0);  
	    return sb;  
	  }
}  