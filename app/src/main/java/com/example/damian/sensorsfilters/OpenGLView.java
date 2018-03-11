package com.example.damian.sensorsfilters;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class OpenGLView extends GLSurfaceView {
    private final OpenGLRenderer renderer;

    OpenGLView(Context context){
        super(context);
        renderer = new OpenGLRenderer(context);
        setRenderer(renderer);
    }

    public OpenGLRenderer getRenderer(){
        return renderer;
    }

}
