package netty;

/**
 * Created by Administrator on 2015/12/8.
 */
public class TestConfig {

	public enum FrameType{LengthField,Delimiter}

	public static FrameType current_frametype = FrameType.Delimiter;

	public enum MsgType{String,Byte}
	public static MsgType current_msgtype = MsgType.String;
}
