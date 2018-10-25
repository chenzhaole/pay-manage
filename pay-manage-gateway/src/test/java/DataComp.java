import com.sys.common.util.DateUtils2;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataComp {


    public static void main(String[] args) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse("2018-10-18 11:43:16");

        String startHHmm = "01:00:00";
        String endHHmm = "10:00:00";

        System.out.println("******" + outOfTime(startHHmm, endHHmm, date.getTime()));

    }


    public static boolean outOfTime(String startHHmmss, String endHHmmss, Long now) throws ParseException {

        System.out.println("判断当日代付时间段,通道交易时间段:" +startHHmmss+ "~"+endHHmmss);
        if (StringUtils.isBlank(startHHmmss) || StringUtils.isBlank(endHHmmss)) {
            return false;
        }
        SimpleDateFormat simpleDateFormatYMD = new SimpleDateFormat("yyyy-MM-dd");
        String yyyyMMdd = simpleDateFormatYMD.format(new Date());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startUtc = simpleDateFormat.parse(yyyyMMdd + " " + startHHmmss );
        Date endUtc = simpleDateFormat.parse(yyyyMMdd + " " + endHHmmss );

        if (startUtc.getTime() > endUtc.getTime()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endUtc);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            System.out.println("yyyymmddhhmmss======" + simpleDateFormat.format(calendar.getTime()));

            System.out.println("判断当日代付时间段,通道交易时间段(UTC时间):"+startUtc+" ~ "+endUtc+",当前UTC时间:"+now+"");
            if (startUtc.getTime() <= now &&  now <= calendar.getTime().getTime()) {
                return true;
            }else{
                return false;
            }
        }else {
            if (startUtc.getTime() <= now &&  now <= endUtc.getTime()) {
                System.out.println("判断当日代付时间段,通道交易时间段(UTC时间):"+startUtc+"-23:59"+" ~ " +"0:00-"+endUtc +",当前UTC时间:"+now+"");
                return true;
            }else{
                return  false;
            }
        }
    }



}
