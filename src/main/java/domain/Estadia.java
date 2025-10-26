package domain;
import java.time.LocalDate;

public class Estadia {
    LocalDate checkIn;
    LocalDate checkOut;    

    public Estadia(LocalDate checkIn, LocalDate checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }
    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setcheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    public void setcheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
}
