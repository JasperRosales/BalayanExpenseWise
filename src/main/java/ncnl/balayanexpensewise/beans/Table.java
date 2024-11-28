    package ncnl.balayanexpensewise.beans;

    public class Table {

        /**
         * Gets the table name based on the ComboBox value.
         *
         * @param tablePath the value selected in the argument
         * @return the corresponding table name
         * @throws IllegalArgumentException if the String value is invalid
         */
        public static String getTableName(String tablePath) {
            return switch (tablePath.toLowerCase()) {
                case "ssc" -> "ssc_transactions";
                case "cics" -> "cics_transactions";
                case "cet" -> "cet_transactions";
                default -> throw new IllegalArgumentException("Invalid value: " + tablePath);
            };
        }
    }
