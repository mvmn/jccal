package x.mvmn.util.dates;

public interface DateComparable<T> {

	public boolean isSame(DateComparable<T> comparison);

	public boolean isBefore(DateComparable<T> comparison);

	public boolean isAfter(DateComparable<T> comparison);

	public int compareTo(DateComparable<T> dateComparable);

	public T getThis();
}
