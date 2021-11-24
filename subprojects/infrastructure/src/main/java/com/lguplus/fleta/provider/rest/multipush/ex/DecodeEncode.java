package com.lguplus.fleta.provider.rest.multipush.ex;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

@Slf4j
public class DecodeEncode {//implements RpcCodec {

/*
    static final int GZIP = 1;
    static final int SNAPPY = 2;
    static final int AES = 1;

    ServiceMetas serviceMetas;

    private byte[] reqHeartBeatBytes;
    private byte[] resHeartBeatBytes;

    private ZipUnzip zlib;
    private ZipUnzip snappy;
    private Crypt aes;

    private HashMap<Integer, Integer> zipMap = new HashMap<>(); // only for encode
    private HashMap<Integer, Integer> minSizeToZipMap = new HashMap<>(); // only for encode

    public DecodeEncode(ServiceMetas serviceMetas) {

        zlib = new Zlib();
        snappy = loadSnappy();  // continue if missing org.xerial.snappy dependence
        aes = new Aes();

        this.serviceMetas = serviceMetas;
        RpcData reqHeartBeatData = new RpcData(
                RpcMeta.newBuilder().setDirection(RpcMeta.Direction.REQUEST).setServiceId(1).setMsgId(1).build());
        ByteBuf bb = Unpooled.buffer(32);
        encode(reqHeartBeatData, bb,null);
        reqHeartBeatBytes = new byte[bb.readableBytes()];
        bb.readBytes(reqHeartBeatBytes);
        bb.clear();
        RpcData resHeartBeatData = new RpcData(
                RpcMeta.newBuilder().setDirection(RpcMeta.Direction.RESPONSE).setServiceId(1).setMsgId(1).build());
        encode(resHeartBeatData, bb,null);
        resHeartBeatBytes = new byte[bb.readableBytes()];
        bb.readBytes(resHeartBeatBytes);
        ReferenceCountUtil.release(bb);
    }

    public void configZip(int serviceId, int zip, int minSizeToZip) {
        zipMap.put(serviceId, zip);
        minSizeToZipMap.put(serviceId, minSizeToZip);
    }

    public RpcMeta decodeMeta(ByteBuf bb) {

        int len = bb.readableBytes();
        if (len < 8) {
            throw new RuntimeException("package_len_error");
        }

        int b0 = bb.readByte();
        int b1 = bb.readByte();
        int metaLen = bb.readShort();
        int packageLen = bb.readInt();

        if (b0 != 'K' && b1 != 'R') {
            throw new RuntimeException("package_flag_error");
        }
        if (metaLen <= 0 || metaLen > packageLen || packageLen + 8 != len) {
            throw new RuntimeException("package_len_not_match");
        }

        RpcMeta meta = null;
        try {
            ByteBuf metaBytes = bb.readSlice(metaLen);
            ByteBufInputStream is = new ByteBufInputStream(metaBytes);
            meta = RpcMeta.parseFrom(is);
        } catch (Exception e) {
            throw new RuntimeException("decode_meta_error");
        }
        if (meta.getDirection() == RpcMeta.Direction.INVALID_DIRECTION) {
            throw new RuntimeException("package_direction_error");
        }
        if (meta.getServiceId() < 1) {
            throw new RuntimeException("package_serviceId_error");
        }
        if (meta.getMsgId() < 1) {
            throw new RuntimeException("package_msgId_error");
        }

        return meta;
    }

    public RpcData decodeBodyAsByteBuf(RpcMeta meta, ByteBuf leftBuff,String key) {
        int left = leftBuff.readableBytes();
        ByteBuf bodyBb = null;
        if (left > 0 && meta.getEncrypt() > 0) {

            if( isEmpty(key) ) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt key not found to encode package");
                else
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt key not found to encode package");
            }

            byte[] bytes = new byte[left];
            leftBuff.readBytes(bytes);
            try {
                byte[] decryptBytes = decrypt(meta.getEncrypt(), bytes, key);

                if( decryptBytes == null ) {
                    if (isRequest(meta))
                        throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt type not supported, type="+meta.getEncrypt());
                    else
                        throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt type not supported, type="+meta.getEncrypt());
                }

                bodyBb = Unpooled.wrappedBuffer(decryptBytes);
            } catch (Exception e) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt request exception, e="+e.getMessage());
                else
                    throw new RpcException(RetCodes.DECODE_RES_ERROR, "decrypt response exception, e="+e.getMessage());
            }
        } else if (left > 0 && meta.getCompress() > 0) {
            byte[] bytes = new byte[left];
            leftBuff.readBytes(bytes);
            try {
                byte[] unzipBytes = unzip(meta.getCompress(), bytes);
                bodyBb = Unpooled.wrappedBuffer(unzipBytes);
            } catch (Exception e) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decode request exception");
                else
                    throw new RpcException(RetCodes.DECODE_RES_ERROR, "decode response exception");
            }
        } else {
            if (left > 0) {
                byte[] bytes = new byte[left];
                leftBuff.readBytes(bytes);
                bodyBb = Unpooled.wrappedBuffer(bytes);
            }
        }

        if( bodyBb == null ) bodyBb = Unpooled.EMPTY_BUFFER;
        return new RpcData(meta, bodyBb);
    }

    public RpcData decodeBodyAsMessage(RpcMeta meta, ByteBuf leftBuff, String key) {
        int left = leftBuff.readableBytes();
        ByteBuf bodyBb = null;
        if (left > 0 && meta.getEncrypt() > 0) {

            if( isEmpty(key) ) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt key not found to encode package");
                else
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt key not found to encode package");
            }

            byte[] bytes = new byte[left];
            leftBuff.readBytes(bytes);
            try {
                byte[] decryptBytes = decrypt(meta.getEncrypt(), bytes, key);

                if( decryptBytes == null ) {
                    if (isRequest(meta))
                        throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt type not supported, type="+meta.getEncrypt());
                    else
                        throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt type not supported, type="+meta.getEncrypt());
                }

                bodyBb = Unpooled.wrappedBuffer(decryptBytes);
            } catch (Exception e) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decrypt request exception, e="+e.getMessage());
                else
                    throw new RpcException(RetCodes.DECODE_RES_ERROR, "decrypt response exception, e="+e.getMessage());
            }
        } else if (left > 0 && meta.getCompress() > 0) {
            byte[] bytes = new byte[left];
            leftBuff.readBytes(bytes);
            try {
                byte[] unzipBytes = unzip(meta.getCompress(), bytes);
                bodyBb = Unpooled.wrappedBuffer(unzipBytes);
            } catch (Exception e) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decode request exception");
                else
                    throw new RpcException(RetCodes.DECODE_RES_ERROR, "decode response exception");
            }
        } else {
            if (left > 0) {
                bodyBb = leftBuff.readSlice(left);
            } else {
                if (!isRequest(meta)) {
                    Message res = serviceMetas.generateRes(meta.getServiceId(), meta.getMsgId(),
                            meta.getRetCode());
                    return new RpcData(meta, res);
                }
            }
        }

        Method m = null;
        if (isRequest(meta)) {
            m = serviceMetas.findReqParser(meta.getServiceId(), meta.getMsgId());
        } else {
            m = serviceMetas.findResParser(meta.getServiceId(), meta.getMsgId());
        }

        if (m != null) {
            Message res = null;
            try {
                ByteBufInputStream is = new ByteBufInputStream(bodyBb == null ? Unpooled.EMPTY_BUFFER : bodyBb);
                res = (Message) m.invoke(null, is);
            } catch (Exception e) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decode request exception");
                else
                    throw new RpcException(RetCodes.DECODE_RES_ERROR, "decode response exception");
            }
            return new RpcData(meta, res);

        } else {
            Descriptor desc = null;
            if (isRequest(meta)) {
                desc = serviceMetas.findDynamicReqDescriptor(meta.getServiceId(), meta.getMsgId());
            } else {
                desc = serviceMetas.findDynamicResDescriptor(meta.getServiceId(), meta.getMsgId());
            }
            if (desc == null) {
                throw new RpcException(RetCodes.NOT_FOUND, "service_not_found");
            }

            DynamicMessage res = null;
            try {
                ByteBufInputStream is = new ByteBufInputStream(bodyBb == null ? Unpooled.EMPTY_BUFFER : bodyBb);
                res = DynamicMessage.parseFrom(desc, is);
            } catch (Exception e) {
                if (isRequest(meta))
                    throw new RpcException(RetCodes.DECODE_REQ_ERROR, "decode request exception");
                else
                    throw new RpcException(RetCodes.DECODE_RES_ERROR, "decode response exception");
            }
            return new RpcData(meta, res);
        }
    }

    // 对raw body，只有打日志,lb,router才需要反序列化获取里面的值，这里是复制原来的ByteBuf, 不会影响原来的指针位置
    public Message decodeRawBody(RpcMeta meta, ByteBuf bodyBb) {
        if( bodyBb == null ) return null;
        if( bodyBb.writerIndex() == 0 ) return null; // 对 raw body 这里总是一个固定的数组，因为raw body也是这个类生成的

        Method m = null;
        if (isRequest(meta)) {
            m = serviceMetas.findReqParser(meta.getServiceId(), meta.getMsgId());
        } else {
            m = serviceMetas.findResParser(meta.getServiceId(), meta.getMsgId());
        }

        if (m == null) return null;

        try {
            byte[] data = bodyBb.array();
            ByteBuf bb = Unpooled.wrappedBuffer(data,0,bodyBb.writerIndex());
            ByteBufInputStream is = new ByteBufInputStream(bb);
            Message res = (Message) m.invoke(null, is);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public int getSize(RpcData data) {
        int bodyBytesLen = data.getBody() == null ? 0 : ( data.getBody() instanceof Message ? data.asMessage().getSerializedSize() : data.asByteBuf().readableBytes() );
        int metaBytesLen = data.getMeta().getSerializedSize();
        int len = 8 + metaBytesLen + bodyBytesLen;
        return len;
    }

    // 这里的 ByteBuf 读指针会保留，ByteBuf可以重复读
    public void encode(RpcData data, ByteBuf bb,String key) {

        int bodyBytesLen = data.getBody() == null ? 0 : (data.getBody() instanceof Message ? data.asMessage().getSerializedSize() : data.asByteBuf().readableBytes());

        Integer zip = zipMap.get(data.getMeta().getServiceId());
        Integer minSizeToZip = minSizeToZipMap.get(data.getMeta().getServiceId());

        if(data.getBody() != null && data.getMeta().getEncrypt() > 0 ) {

            if( isEmpty(key) ) {
                if (isRequest(data.getMeta()))
                    throw new RpcException(RetCodes.ENCODE_REQ_ERROR, "crypt key not found to encode package");
                else
                    throw new RpcException(RetCodes.ENCODE_RES_ERROR, "crypt key not found to encode package");
            }

            try {
                byte[] body;
                if( data.getBody() instanceof Message ) {
                    body = data.asMessage().toByteArray();
                } else {
                    ByteBuf srcbb = data.asByteBuf();
                    int n = srcbb.readerIndex();
                    body = new byte[srcbb.readableBytes()];
                    srcbb.readBytes(body);
                    srcbb.readerIndex(n);
                }
                byte[] encBody = encrypt(data.getMeta().getEncrypt(), body, key);
                if( encBody == null ) {
                    if (isRequest(data.getMeta()))
                        throw new RpcException(RetCodes.ENCODE_REQ_ERROR, "crypt type not supported, type="+data.getMeta().getEncrypt());
                    else
                        throw new RpcException(RetCodes.ENCODE_RES_ERROR, "crypt type not supported, type="+data.getMeta().getEncrypt());
                }

                int metaBytesLen = data.getMeta().getSerializedSize();
                int len = 8 + metaBytesLen + encBody.length;
                bb.capacity(len);

                ByteBufOutputStream os = new ByteBufOutputStream(bb);
                os.writeByte('K');
                os.writeByte('R');
                os.writeShort(metaBytesLen);
                os.writeInt(len - 8);
                data.getMeta().writeTo(os);
                os.write(encBody);
            } catch (Exception e) {
                if (isRequest(data.getMeta()))
                    throw new RpcException(RetCodes.ENCODE_REQ_ERROR, "encrypt request exception, e="+e.getMessage());
                else
                    throw new RpcException(RetCodes.ENCODE_RES_ERROR, "encrypt response exception, e="+e.getMessage());
            }

            return;
        }

        if (data.getBody() != null && zip != null && zip.intValue() > 0 && minSizeToZip != null && bodyBytesLen > minSizeToZip.intValue()) {

            try {

                byte[] body;
                if( data.getBody() instanceof Message ) {
                    body = data.asMessage().toByteArray();
                } else {
                    ByteBuf srcbb = data.asByteBuf();
                    int n = srcbb.readerIndex();
                    body = new byte[srcbb.readableBytes()];
                    srcbb.readBytes(body);
                    srcbb.readerIndex(n);
                }

                byte[] encBody = zip(zip, body);
                ReflectionUtils.updateCompress(data.getMeta(), zip);

                int metaBytesLen = data.getMeta().getSerializedSize();
                int len = 8 + metaBytesLen + encBody.length;
                bb.capacity(len);

                ByteBufOutputStream os = new ByteBufOutputStream(bb);
                os.writeByte('K');
                os.writeByte('R');
                os.writeShort(metaBytesLen);
                os.writeInt(len - 8);
                data.getMeta().writeTo(os);
                os.write(encBody);
            } catch (Exception e) {
                if (isRequest(data.getMeta()))
                    throw new RpcException(RetCodes.ENCODE_REQ_ERROR, "encode request exception");
                else
                    throw new RpcException(RetCodes.ENCODE_RES_ERROR, "encode response exception");
            }

            return;
        }

        int metaBytesLen = data.getMeta().getSerializedSize();
        int len = 8 + metaBytesLen + bodyBytesLen;
        bb.capacity(len);

        try {
            ByteBufOutputStream os = new ByteBufOutputStream(bb);
            os.writeByte('K');
            os.writeByte('R');
            os.writeShort(metaBytesLen);
            os.writeInt(len - 8);
            data.getMeta().writeTo(os);
            if (data.getBody() != null) {
                if( data.getBody() instanceof Message ) {
                    data.asMessage().writeTo(os);
                } else {
                    ByteBuf srcbb = data.asByteBuf();
                    int n = srcbb.readerIndex();
                    byte[] bodyBytes = srcbb.array();
                    if( bodyBytes.length != srcbb.readableBytes() ) {
                        bodyBytes = new byte[srcbb.readableBytes()];
                        srcbb.readBytes(bodyBytes);
                    }
                    srcbb.readerIndex(n);
                    os.write(bodyBytes);
                }
            }
        } catch (Exception e) {
            if (isRequest(data.getMeta()))
                throw new RpcException(RetCodes.ENCODE_REQ_ERROR, "encode request exception");
            else
                throw new RpcException(RetCodes.ENCODE_RES_ERROR, "encode response exception");
        }
    }


    public byte[] encrypt(int type, byte[] data, String key) {
        switch (type) {
            case AES:
                return aes.encrypt(data,key);
            default:
                return null;
        }
    }

    public byte[] decrypt(int type, byte[] data, String key)  {
        switch (type) {
            case AES:
                return aes.decrypt(data,key);
            default:
                return null;
        }
    }

    public byte[] zip(int zipType, byte[] data) throws IOException {
        switch (zipType) {
            case GZIP:
                return zlib.zip(data);
            case SNAPPY:
                if (snappy != null) {
                    return snappy.zip(data);
                } else {
                    log.error("snappy not found, data not zipped");
                    return data;
                }
            default:
                return data;
        }
    }

    public byte[] unzip(int zipType, byte[] data) throws IOException {
        switch (zipType) {
            case GZIP:
                return zlib.unzip(data);
            case SNAPPY:
                if (snappy != null) {
                    return snappy.unzip(data);
                } else {
                    log.error("snappy not found, data not unzipped");
                    return data;
                }
            default:
                return data;
        }
    }

    public void getReqHeartBeat(ByteBuf bb) {
        bb.writeBytes(reqHeartBeatBytes);
    }

    public void getResHeartBeat(ByteBuf bb) {
        bb.writeBytes(resHeartBeatBytes);
    }

    ZipUnzip loadSnappy() {
        try {
            Class<?> cls = Class.forName("krpc.rpc.util.compress.Snappy");
            return (ZipUnzip) cls.newInstance();
        } catch (Throwable e) {
            log.error("snappy not loaded");
            return null;
        }
    }

    public boolean isRequest(RpcMeta meta) {
        return meta.getDirection() == RpcMeta.Direction.REQUEST;
    }

    public boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public ServiceMetas getServiceMetas() {
        return serviceMetas;
    }

    public void setServiceMetas(ServiceMetas serviceMetas) {
        this.serviceMetas = serviceMetas;
    }
*/
}