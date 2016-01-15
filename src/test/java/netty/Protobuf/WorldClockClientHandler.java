package netty.Protobuf;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/12/11.
 */
public class WorldClockClientHandler extends SimpleChannelInboundHandler<WorldClockProtocol.LocalTimes>{

	private static final Pattern DELIM = Pattern.compile("/");

	private volatile Channel channel;
	private final BlockingQueue<WorldClockProtocol.LocalTimes> answer = new LinkedBlockingQueue<WorldClockProtocol.LocalTimes>();

	public WorldClockClientHandler(){super(false); }

	public List<String> getLocalTimes(Collection<String> cities){
		WorldClockProtocol.Locations.Builder builder = WorldClockProtocol.Locations.newBuilder();
		for(String c: cities){
			String[] components = DELIM.split(c);
			builder.addLocation(WorldClockProtocol.Location.newBuilder().setContinent(WorldClockProtocol.Continent.valueOf(components[0].toUpperCase()))
			.setCity(components[1]).build());
		}
		channel.writeAndFlush(builder.build());

		WorldClockProtocol.LocalTimes localTimes;
		boolean interrupted = false;
		for (;;) {
			try {
				localTimes = answer.take();
				break;
			} catch (InterruptedException ignore) {
				interrupted = true;
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}

		List<String> result = new ArrayList<String>();
		for (WorldClockProtocol.LocalTime lt: localTimes.getLocalTimeList()) {
			result.add(
					new Formatter().format(
							"%4d-%02d-%02d %02d:%02d:%02d %s",
							lt.getYear(),
							lt.getMonth(),
							lt.getDayOfMonth(),
							lt.getHour(),
							lt.getMinute(),
							lt.getSecond(),
							lt.getDayOfWeek().name()).toString());
		}

		return result;

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WorldClockProtocol.LocalTimes msg) throws Exception {
		System.out.println("this is channel Read 0000000000");
		answer.add(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		super.exceptionCaught(ctx, cause);
		System.out.println("an exception is boooooooooooooom");
		cause.printStackTrace();
		ctx.close();
	}
//
//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
////		super.channelReadComplete(ctx);
//		ctx.flush();
//	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//		super.channelRegistered(ctx);
		channel = ctx.channel();
	}
}
