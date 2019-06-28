package fr.d2factory.libraryapp.member;

public class Student extends Member{
    private int year;

    public Student(long id, int year)
    {
        this.id = id;
        this.setYear(year);
    }

    private static final float PRICE_IN_NORMAL = 0.1f;
    private static final float PRICE_IN_LATE = 0.15f;

    @Override
    public void payBook(int numberOfDays) {
        int numberOfChargedDays;

        if(this.year!=1){
            numberOfChargedDays = numberOfDays;
        }else{
            if (numberOfDays > 15){
                numberOfChargedDays = numberOfDays-15;
            }else{
                numberOfChargedDays = 0;
            }
        }

        if(numberOfDays < 30){
            setWallet(getWallet()-numberOfChargedDays* PRICE_IN_NORMAL);
        }else{
            setWallet(getWallet()-((numberOfChargedDays-numberOfDays+30)* PRICE_IN_NORMAL +(numberOfDays-30)* PRICE_IN_LATE));
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
