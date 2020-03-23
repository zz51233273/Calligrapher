package com.example.hasee.bluecalligrapher.encodebase64;



import com.example.hasee.bluecalligrapher.decodebase64.MyStringUtils;

import java.util.Arrays;

/**
 * Created by hasee on 2018/4/20.
 */

public abstract class MyBaseNCodec implements MyBinaryEncoder {
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
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, (byte)61);
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

    public Object encode(Object obj) throws MyEncoderException {
        if(!(obj instanceof byte[])) {
            throw new MyEncoderException("Parameter supplied to Base-N encode is not a byte[]");
        } else {
            return this.encode((byte[])((byte[])obj));
        }
    }

    public String encodeToString(byte[] pArray) {
        return MyStringUtils.newStringUtf8(this.encode(pArray));
    }

    public String encodeAsString(byte[] pArray) {
        return MyStringUtils.newStringUtf8(this.encode(pArray));
    }

    public byte[] encode(byte[] pArray) {
        return pArray != null && pArray.length != 0?this.encode(pArray, 0, pArray.length):pArray;
    }

    public byte[] encode(byte[] pArray, int offset, int length) {
        if(pArray != null && pArray.length != 0) {
            MyBaseNCodec.Context context = new MyBaseNCodec.Context();
            this.encode(pArray, offset, length, context);
            this.encode(pArray, offset, -1, context);
            byte[] buf = new byte[context.pos - context.readPos];
            this.readResults(buf, 0, buf.length, context);
            return buf;
        } else {
            return pArray;
        }
    }

    abstract void encode(byte[] var1, int var2, int var3, MyBaseNCodec.Context var4);

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

    public long getEncodedLength(byte[] pArray) {
        long len = (long)((pArray.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize) * (long)this.encodedBlockSize;
        if(this.lineLength > 0) {
            len += (len + (long)this.lineLength - 1L) / (long)this.lineLength * (long)this.chunkSeparatorLength;
        }

        return len;
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
