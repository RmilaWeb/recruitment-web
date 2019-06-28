package fr.d2factory.libraryapp.member;

public class Resident extends Member{

    public Resident(long id)
    {
        this.id = id;
    }

    private static final float PRICE_IN_NORMAL = 0.1f;
    private static final float PRICE_IN_LATE = 0.2f;

    @Override
    public void payBook(int numberOfDays) {
        if(numberOfDays < 60){
            setWallet(getWallet()-numberOfDays* PRICE_IN_NORMAL);
        }else{
            setWallet(getWallet()-(60* PRICE_IN_NORMAL +(numberOfDays-60)* PRICE_IN_LATE));
        }
    }
}
