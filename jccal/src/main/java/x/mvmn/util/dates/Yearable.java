package x.mvmn.util.dates;

public interface Yearable {
	public Yearable setYearValue(int year);

	public Yearable addYearValue(int delta);

	public int getYearValue();

	public Yearable getYear();
}
