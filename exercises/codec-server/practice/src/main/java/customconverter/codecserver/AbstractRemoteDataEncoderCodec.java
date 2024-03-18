package customconverter.codecserver;

import com.google.protobuf.util.JsonFormat;
import java.util.Arrays;
import java.util.ArrayList;

public abstract class AbstractRemoteDataEncoderCodec {
    public static final ArrayList<String> NAMESPACES = new ArrayList<String>(Arrays.asList("default"));
    public static final String ENCODE_PATH_POSTFIX = "/encode";
    public static final String DECODE_PATH_POSTFIX = "/decode";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    public static final JsonFormat.Parser JSON_FORMAT = JsonFormat.parser();
    public static final JsonFormat.Printer JSON_PRINTER = JsonFormat.printer();

}
