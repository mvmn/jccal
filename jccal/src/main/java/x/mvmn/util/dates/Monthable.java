package x.mvmn.util.dates;

public interface Monthable {
	public Monthable setMonthValue(int month);

	public Monthable addMonthValue(int delta);

	public int getMonthValue();

	public Monthable getMonth();
}
