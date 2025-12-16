
public class Console {

    /**
     * Clear input.
     * @param msg   Message.
     */
    public static void clearInput() {
        String clearSequence = "\033[1A\033[2K";
        System.out.print(clearSequence);
        System.out.print("\r");
    }
}
