package com.example.damian.sensorsfilters;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class OpenGLView2 extends GLSurfaceView {
    private final OpenGLRenderer2 renderer;

    OpenGLView2(Context context){
        super(context);
        renderer = new OpenGLRenderer2(context);
        setRenderer(renderer);
    }

    public OpenGLRenderer2 getRenderer(){
        return renderer;
    }

}
