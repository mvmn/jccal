package x.mvmn.util.dates;

public interface Dayable {
	public Dayable setDayValue(int day);

	public Dayable addDayValue(int delta);

	public int getDayValue();

	public Dayable getDay();
}
