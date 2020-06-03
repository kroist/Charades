package com.charades.client;


import java.io.Serializable;

public abstract class AbstractImageLoader implements Serializable {



    protected int height = -1;
    protected int width = -1;
    protected int channels = -1;
    protected boolean centerCropIfNeeded = false;




}

