package com.charades.client;


import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.nd4j.common.util.ArrayUtil;
import org.nd4j.linalg.api.concurrency.AffinityManager;
import org.nd4j.linalg.api.memory.pointers.PagedPointer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.image.BufferedImage;
import java.io.*;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Uses JavaCV to load images. Allowed formats: bmp, gif, jpg, jpeg, jp2, pbm, pgm, ppm, pnm, png, tif, tiff, exr, webp
 *
 * @author saudet
 */
public class MyImageLoader extends AbstractImageLoader {


    protected OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

    public MyImageLoader(int height, int width, int channels) {
        this.height = height;
        this.width = width;
        this.channels = channels;
    }
    public MyImageLoader(int height, int width, int channels, boolean centerCropIfNeeded) {
        this(height, width, channels);
        this.centerCropIfNeeded = centerCropIfNeeded;
    }


    public INDArray asRowVector(BufferedImage image) throws IOException {
        return asMatrix(image).ravel();
    }

    Java2DFrameConverter converter2 = new Java2DFrameConverter();


    public INDArray asMatrix(BufferedImage image) throws IOException {
        INDArray array = asMatrix(converter.convert(converter2.getFrame(image, 1.0, false)));
        return array;
    }

    protected Mat centerCropIfNeeded(Mat img) {
        int x = 0;
        int y = 0;
        int height = img.rows();
        int width = img.cols();
        int diff = Math.abs(width - height) / 2;

        if (width > height) {
            x = diff;
            width = width - diff;
        } else if (height > width) {
            y = diff;
            height = height - diff;
        }
        return img.apply(new Rect(x, y, width, height));
    }

    protected Mat scalingIfNeed(Mat image) {
        return scalingIfNeed(image, height, width);
    }

    protected Mat scalingIfNeed(Mat image, int dstHeight, int dstWidth) {
        Mat scaled = image;
        if (dstHeight > 0 && dstWidth > 0 && (image.rows() != dstHeight || image.cols() != dstWidth)) {
            resize(image, scaled = new Mat(), new Size(dstWidth, dstHeight));
        }
        return scaled;
    }

    protected void fillNDArray(Mat image, INDArray ret) {
        int rows = image.rows();
        int cols = image.cols();
        int channels = image.channels();

        if (ret.length() != rows * cols * channels) {
            throw new ND4JIllegalStateException("INDArray provided to store image not equal to image: {channels: "
                    + channels + ", rows: " + rows + ", columns: " + cols + "}");
        }

        boolean direct = !Loader.getPlatform().startsWith("android");
        Indexer idx = image.createIndexer(direct);
        Pointer pointer = ret.data().pointer();
        long[] stride = ret.stride();
        boolean done = false;
        PagedPointer pagedPointer = new PagedPointer(pointer, rows * cols * channels,
                ret.data().offset() * Nd4j.sizeOfDataType(ret.data().dataType()));

        if (pointer instanceof FloatPointer) {
            FloatIndexer retidx = FloatIndexer.create((FloatPointer) pagedPointer.asFloatPointer(),
                    new long[] {channels, rows, cols}, new long[] {stride[0], stride[1], stride[2]}, direct);
            if (idx instanceof UByteIndexer) {
                UByteIndexer ubyteidx = (UByteIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, ubyteidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            } else if (idx instanceof UShortIndexer) {
                UShortIndexer ushortidx = (UShortIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, ushortidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            } else if (idx instanceof IntIndexer) {
                IntIndexer intidx = (IntIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, intidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            } else if (idx instanceof FloatIndexer) {
                FloatIndexer floatidx = (FloatIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, floatidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            }
        } else if (pointer instanceof DoublePointer) {
            DoubleIndexer retidx = DoubleIndexer.create((DoublePointer) pagedPointer.asDoublePointer(),
                    new long[] {channels, rows, cols}, new long[] {stride[0], stride[1], stride[2]}, direct);
            if (idx instanceof UByteIndexer) {
                UByteIndexer ubyteidx = (UByteIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, ubyteidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            } else if (idx instanceof UShortIndexer) {
                UShortIndexer ushortidx = (UShortIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, ushortidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            } else if (idx instanceof IntIndexer) {
                IntIndexer intidx = (IntIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, intidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            } else if (idx instanceof FloatIndexer) {
                FloatIndexer floatidx = (FloatIndexer) idx;
                for (int k = 0; k < channels; k++) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                            retidx.put(k, i, j, floatidx.get(i, j, k));
                        }
                    }
                }
                done = true;
            }
        }


        if (!done) {
            for (int k = 0; k < channels; k++) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (channels > 1) {
                            ret.putScalar(k, i, j, idx.getDouble(i, j, k));
                        } else {
                            ret.putScalar(i, j, idx.getDouble(i, j));
                        }
                    }
                }
            }
        }

        image.data();
        Nd4j.getAffinityManager().tagLocation(ret, AffinityManager.Location.HOST);
    }




    protected INDArray transformImage(Mat image, INDArray ret) throws IOException {
        Mat image2 = null, image3 = null, image4 = null;

        if (channels > 0 && image.channels() != channels) {
            int code = -1;
            switch (image.channels()) {
                case 1:
                    switch (channels) {
                        case 3:
                            code = CV_GRAY2BGR;
                            break;
                        case 4:
                            code = CV_GRAY2RGBA;
                            break;
                    }
                    break;
                case 3:
                    switch (channels) {
                        case 1:
                            code = CV_BGR2GRAY;
                            break;
                        case 4:
                            code = CV_BGR2RGBA;
                            break;
                    }
                    break;
                case 4:
                    switch (channels) {
                        case 1:
                            code = CV_RGBA2GRAY;
                            break;
                        case 3:
                            code = CV_RGBA2BGR;
                            break;
                    }
                    break;
            }
            if (code < 0) {
                throw new IOException("Cannot convert from " + image.channels() + " to " + channels + " channels.");
            }
            image2 = new Mat();
            cvtColor(image, image2, code);
            image = image2;
        }
        if (centerCropIfNeeded) {
            image3 = centerCropIfNeeded(image);
            if (image3 != image) {
                image = image3;
            } else {
                image3 = null;
            }
        }
        image4 = scalingIfNeed(image);
        if (image4 != image) {
            image = image4;
        } else {
            image4 = null;
        }

        if (ret == null) {
            int rows = image.rows();
            int cols = image.cols();
            int channels = image.channels();
            ret = Nd4j.create(channels, rows, cols);
        }
        fillNDArray(image, ret);

        image.data(); // dummy call to make sure it does not get deallocated prematurely
        if (image2 != null) {
            image2.deallocate();
        }
        if (image3 != null) {
            image3.deallocate();
        }
        if (image4 != null) {
            image4.deallocate();
        }
        return ret;
    }

    public INDArray asMatrix(Mat image) throws IOException {
        INDArray ret = transformImage(image, null);

        return ret.reshape(ArrayUtil.combine(new long[] {1}, ret.shape()));
    }


}

