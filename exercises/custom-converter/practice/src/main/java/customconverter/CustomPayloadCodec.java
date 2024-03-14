package customconverter;

import com.google.protobuf.ByteString;
import io.temporal.api.common.v1.Payload;
import io.temporal.payload.codec.PayloadCodec;
import io.temporal.common.converter.DataConverterException;
import io.temporal.common.converter.EncodingKeys;
import io.temporal.payload.codec.PayloadCodecException;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.xerial.snappy.Snappy;


class CustomPayloadCodec implements PayloadCodec {

  private static final Charset UTF_8 = StandardCharsets.UTF_8;

  static final ByteString METADATA_ENCODING =
      ByteString.copyFrom("binary/snappy", UTF_8);
  
  @Override
  public List<Payload> encode( List<Payload> payloads) {
    // TODO: Add the appropriate call to the encodePayload method below, then
    // uncomment the return statement.
    // return payloads.stream().map().collect(Collectors.toList());
  }

  @Override
  public List<Payload> decode( List<Payload> payloads) {
    return payloads.stream().map(this::decodePayload).collect(Collectors.toList());
  }

  private Payload encodePayload(Payload payload) {

    byte[] compressedData;

    try {
      compressedData = Snappy.compress(payload.toByteArray());
    } catch (Throwable e) {
      throw new DataConverterException(e);
    }

    return Payload.newBuilder()
        .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_ENCODING)
        .setData(ByteString.copyFrom(compressedData))
        .build();
  }

  private Payload decodePayload(Payload payload) {
    if (METADATA_ENCODING.equals(payload.getMetadataOrDefault(EncodingKeys.METADATA_ENCODING_KEY, null))) {
      byte[] plainData;
      Payload decodedPayload;
      try {
        plainData = Snappy.uncompress(payload.getData().toByteArray());
        decodedPayload = Payload.parseFrom(plainData);
        return decodedPayload;
      } catch (Throwable e) {
        throw new PayloadCodecException(e);
      }
    } else {
      return payload;
    }
  }

}