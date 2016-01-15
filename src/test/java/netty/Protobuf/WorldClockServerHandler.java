package netty.Protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2015/12/11.
 */
public class WorldClockServerHandler extends SimpleChannelInboundHandler<WorldClockProtocol.Locations> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WorldClockProtocol.Locations msg) throws Exception {
		System.out.println("["+ctx.channel().remoteAddress()+"]:server 's channel is reading....");
		long currentTime = System.currentTimeMillis();
		WorldClockProtocol.LocalTimes.Builder builder = WorldClockProtocol.LocalTimes.newBuilder();
		for(WorldClockProtocol.Location l:msg.getLocationList()){
			TimeZone tz = TimeZone.getTimeZone(toString(l.getContinent())+'/'+l.getCity());
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTimeInMillis(currentTime);
			System.out.print("the info :" + toString(l.getContinent()) + "/" + l.getCity());
//			System.out.println(": "+calendar);
			builder.addLocalTime(WorldClockProtocol.LocalTime.newBuilder().
					setYear(calendar.get(Calendar.YEAR))
					.setMonth(calendar.get(Calendar.MONTH)+1)
					.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
					.setDayOfWeek(WorldClockProtocol.DayOfWeek.valueOf(calendar.get(Calendar.DAY_OF_WEEK)))
					.setHour(calendar.get(Calendar.HOUR_OF_DAY))
					.setMinute(calendar.get(Calendar.MINUTE))
					.setSecond(calendar.get(Calendar.SECOND)).build());

		}
		ctx.writeAndFlush(builder.build());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		super.channelActive(ctx);
		System.out.println("["+ctx.channel().remoteAddress()+"]: server 's channel is active....");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		super.channelReadComplete(ctx);
		System.out.println("["+ctx.channel().remoteAddress()+"]:server has readComplete....");
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}

	private static String toString(WorldClockProtocol.Continent c){
		return c.name().charAt(0)+c.name().toLowerCase().substring(1);
	}
}
