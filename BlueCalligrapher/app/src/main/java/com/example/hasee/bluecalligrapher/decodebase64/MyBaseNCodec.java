package com.example.hasee.bluecalligrapher.decodebase64;

import java.util.Arrays;

/**
 * Created by hasee on 2018/4/3.
 */

public abstract class MyBaseNCodec implements MyBinaryDecoder{
    static final int EOF = -1;
    public static final int MIME_CHUNK_SIZE = 76;
    public static final int PEM_CHUNK_SIZE = 64;
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    protected static final int MASK_8BITS = 255;
    protected static final byte PAD_DEFAULT = 61;
    /** @deprecated */
    @Deprecated
    protected final byte PAD;
    protected final byte pad;
    private final int unencodedBlockSize;
    private final int encodedBlockSize;
    protected final int lineLength;
    private final int chunkSeparatorLength;
    protected MyBaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength) {
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength,(byte)61);
    }

    protected MyBaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength, byte pad) {
        this.PAD = 61;
        this.unencodedBlockSize = unencodedBlockSize;
        this.encodedBlockSize = encodedBlockSize;
        boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
        this.lineLength = useChunking?lineLength / encodedBlockSize * encodedBlockSize:0;
        this.chunkSeparatorLength = chunkSeparatorLength;
        this.pad = pad;
    }

    boolean hasData(MyBaseNCodec.Context context) {
        return context.buffer != null;
    }

    int available(MyBaseNCodec.Context context) {
        return context.buffer != null?context.pos - context.readPos:0;
    }

    protected int getDefaultBufferSize() {
        return 8192;
    }

    private byte[] resizeBuffer(MyBaseNCodec.Context context) {
        if(context.buffer == null) {
            context.buffer = new byte[this.getDefaultBufferSize()];
            context.pos = 0;
            context.readPos = 0;
        } else {
            byte[] b = new byte[context.buffer.length * 2];
            System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
            context.buffer = b;
        }

        return context.buffer;
    }

    protected byte[] ensureBufferSize(int size, MyBaseNCodec.Context context) {
        return context.buffer != null && context.buffer.length >= context.pos + size?context.buffer:this.resizeBuffer(context);
    }

    int readResults(byte[] b, int bPos, int bAvail, MyBaseNCodec.Context context) {
        if(context.buffer != null) {
            int len = Math.min(this.available(context), bAvail);
            System.arraycopy(context.buffer, context.readPos, b, bPos, len);
            context.readPos += len;
            if(context.readPos >= context.pos) {
                context.buffer = null;
            }

            return len;
        } else {
            return context.eof?-1:0;
        }
    }

    protected static boolean isWhiteSpace(byte byteToCheck) {
        switch(byteToCheck) {
            case 9:
            case 10:
            case 13:
            case 32:
                return true;
            default:
                return false;
        }
    }

    public Object decode(Object obj){
        if(obj instanceof byte[]) {
            return this.decode((byte[])((byte[])obj));
        } else if(obj instanceof String) {
            return this.decode((String)obj);
        }
        return null;
    }

    public byte[] decode(String pArray) {
        return this.decode(MyStringUtils.getBytesUtf8(pArray));
    }

    public byte[] decode(byte[] pArray) {
        if(pArray != null && pArray.length != 0) {
            MyBaseNCodec.Context context = new MyBaseNCodec.Context();
            this.decode(pArray, 0, pArray.length, context);
            this.decode(pArray, 0, -1, context);
            byte[] result = new byte[context.pos];
            this.readResults(result, 0, result.length, context);
            return result;
        } else {
            return pArray;
        }
    }


    abstract void decode(byte[] var1, int var2, int var3, MyBaseNCodec.Context var4);

    protected abstract boolean isInAlphabet(byte var1);

    public boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad) {
        byte[] var3 = arrayOctet;
        int var4 = arrayOctet.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte octet = var3[var5];
            if(!this.isInAlphabet(octet) && (!allowWSPad || octet != this.pad && !isWhiteSpace(octet))) {
                return false;
            }
        }

        return true;
    }

    public boolean isInAlphabet(String basen) {
        return this.isInAlphabet(MyStringUtils.getBytesUtf8(basen), true);
    }

    protected boolean containsAlphabetOrPad(byte[] arrayOctet) {
        if(arrayOctet == null) {
            return false;
        } else {
            byte[] var2 = arrayOctet;
            int var3 = arrayOctet.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte element = var2[var4];
                if(this.pad == element || this.isInAlphabet(element)) {
                    return true;
                }
            }

            return false;
        }
    }

    static class Context {
        int ibitWorkArea;
        long lbitWorkArea;
        byte[] buffer;
        int pos;
        int readPos;
        boolean eof;
        int currentLinePos;
        int modulus;

        Context() {
        }

        public String toString() {
            return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", new Object[]{this.getClass().getSimpleName(), Arrays.toString(this.buffer), Integer.valueOf(this.currentLinePos), Boolean.valueOf(this.eof), Integer.valueOf(this.ibitWorkArea), Long.valueOf(this.lbitWorkArea), Integer.valueOf(this.modulus), Integer.valueOf(this.pos), Integer.valueOf(this.readPos)});
        }
    }
}
