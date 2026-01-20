package edu.icet.Exceptions;

public class SeatLockedException extends RuntimeException {
    private final long remainingSeconds;

    public SeatLockedException(long remainingSeconds) {
        super(String.format("Seat is currently held. Try again in %d seconds", remainingSeconds));
        this.remainingSeconds = remainingSeconds;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }
}

