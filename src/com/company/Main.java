package com.company;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class Main {
    //CONSTANTS
    public static final Map<messageCodes, String> constantMessages = Map.ofEntries(
            entry(messageCodes.BREAKLINE, "=================================="),
            entry(messageCodes.INITSEATINGS, "Willkommen, wie viele Sitzplätze stellen Sie zur Verfügung?"),
            entry(messageCodes.MENUTITLE, "|   HAUPTMENU    |"),
            entry(messageCodes.MENUOPTIONS, "Was möchtest du tun?:"),
            entry(messageCodes.BOOKINGUESTS,"Wie viele Gäste sollen gebucht werden? " ),
            entry(messageCodes.NOGUESTINPUT, "Kein Gast eingetragen, kehre zum Hauptmenü zurück."),
            entry(messageCodes.NOSPACE, "Tut mir Leid, aktuell ist nicht genügend Platz vorhanden."),
            entry(messageCodes.BOOKOUTGROUP, "Welche Gruppe soll ausgebucht werden? '0' um abzubrechen. "),
            entry(messageCodes.NOOPTION, "Das ist keine Option, bitte wähle erneut."),
            entry(messageCodes.ADDSEATINGS,"Wie viele Stühle sollen hinzugefügt werden? " ),
            entry(messageCodes.REMOVESEATINGS, "Wie viele Stühle sollen entfernt werden?"),
            entry(messageCodes.REMOVESEATINGSUNAVAILABLE, "Aktuell können keine Stühle entfernt werden"),
            entry(messageCodes.NOTENOUGHSEATINGS, "Es wurden mehr Stühle angegeben als aktuell zur Verfügung stehen, kehre zum Hauptmenü zurück."),
            entry(messageCodes.BACKTOMENU, "Kehre zum Hauptmenü zurück"),
            entry(messageCodes.BYE, "Auf Wiedersehen")
    );
    public enum messageCodes  {BREAKLINE, INITSEATINGS, MENUTITLE, MENUOPTIONS, BOOKINGUESTS, NOGUESTINPUT, NOSPACE, BOOKOUTGROUP, NOOPTION, ADDSEATINGS,REMOVESEATINGS, REMOVESEATINGSUNAVAILABLE,NOTENOUGHSEATINGS, BACKTOMENU, BYE }


    public static void main(String[] args) {
        final ArrayList<String>  mainOptions = new ArrayList<>(Arrays.asList("Einbuchen", "Ausbuchen", "Status","Sitzplätze ändern", "Exit"));
        ArrayList<String> bookOutOptions = new ArrayList<>();
        final ArrayList<String> seatingOptions = new ArrayList<>(Arrays.asList("Stühle hinzufügen", "Stühle entfernen", "Exit"));
        int seating;
        print(constantMessages.get(messageCodes.BREAKLINE));
        print(constantMessages.get(messageCodes.INITSEATINGS));
        seating = selectNumber();
        var groupsOnTable = createIntegerHashMapWithSeating(seating);
        var table = createBooleanArrayWithTrueValues(seating);
        int mainSelection, groupId = 0;
        do{
            print(constantMessages.get(messageCodes.BREAKLINE));
            print(constantMessages.get(messageCodes.MENUTITLE));
            print(constantMessages.get(messageCodes.BREAKLINE));
            print(constantMessages.get(messageCodes.MENUOPTIONS));
            mainSelection = selectOptions(mainOptions);
            switch (mainSelection) {
                case 1 -> {
                    int bookInSelection;
                    print(constantMessages.get(messageCodes.BOOKINGUESTS));
                    bookInSelection = selectNumber();
                    System.out.println(bookInSelection);
                    if (bookInSelection == 0) {
                        print(constantMessages.get(messageCodes.NOGUESTINPUT));
                    }
                    groupId++;
                    boolean accessGranted = registerGuests(bookInSelection, table, groupsOnTable, groupId, bookOutOptions);
                    if (accessGranted) {
                        var set = getKeysByValue(groupsOnTable, groupId);
                        if(isExactlyOne(bookInSelection)) System.out.println("Es wurde " + bookInSelection + " Person mit der Gruppen-ID: " + groupId + " für folgenden Platz gebucht: ");
                        else System.out.println("Es wurden " + bookInSelection + " Personen mit der Gruppen-ID: " + groupId + " für folgende Plätze gebucht: ");
                        set.forEach(entry -> System.out.print(entry + " "));
                        System.out.println();
                    } else {
                        groupId--;
                        print(constantMessages.get(messageCodes.NOSPACE));
                    }
                }
                case 2 -> {
                    int bookOutSelection;
                    do {
                        print(constantMessages.get(messageCodes.BOOKOUTGROUP));
                        bookOutSelection = selectOptions(bookOutOptions);
                        if(bookOutSelection == 0) break;
                        if (bookOutSelection > bookOutOptions.size())
                            print(constantMessages.get(messageCodes.NOOPTION));
                        else {
                            String groupIdToRemove = bookOutOptions.get(bookOutSelection - 1);  //-1 because index starts 0
                            unregisterGuests(groupIdToRemove, table, groupsOnTable, bookOutOptions);
                            break;
                        }
                    } while (bookOutSelection > bookOutOptions.size());
                }
                case 3 -> printSeatings(table, groupsOnTable);

                case 4 -> {
                    int option, amountOfSeatingToChange;
                    do {
                        print(constantMessages.get(messageCodes.MENUOPTIONS));
                        option = selectOptions(seatingOptions);
                        if (option > seatingOptions.size()) print(constantMessages.get(messageCodes.NOOPTION));
                        switch (option) {
                            case 1 -> {
                                print(constantMessages.get(messageCodes.ADDSEATINGS));
                                amountOfSeatingToChange = selectNumber();
                                int indexToInsertSeating = getFirstPossibleSeatPlace(table);
                                table = addSeatingToTable(table, amountOfSeatingToChange, indexToInsertSeating, groupsOnTable);
                                groupsOnTable = addSeatingToMap(groupsOnTable, amountOfSeatingToChange, indexToInsertSeating);
                            }
                            case 2 -> {
                                if (areAllFalse(table)) {
                                    print(constantMessages.get(messageCodes.REMOVESEATINGSUNAVAILABLE));
                                    print(constantMessages.get(messageCodes.BACKTOMENU));
                                    break;
                                }
                                print(constantMessages.get(messageCodes.REMOVESEATINGS));
                                amountOfSeatingToChange = selectNumber();
                                if (amountOfSeatingToChange > table.length) {
                                    print(constantMessages.get(messageCodes.NOTENOUGHSEATINGS));
                                    break;
                                }
                                if (ableToRemoveAmountFromTable(table, amountOfSeatingToChange)) {
                                    var list = getBestSeatingOption(amountOfSeatingToChange,table);
                                    table = removeSeatingFromTable(table, amountOfSeatingToChange, list);
                                    groupsOnTable = removeSeatingFromMap(groupsOnTable, amountOfSeatingToChange, list);
                                    if(isExactlyOne(amountOfSeatingToChange)) System.out.println("Es wurde "+amountOfSeatingToChange+ "Stuhl entfernt.");
                                    else System.out.println("Es wurden " + amountOfSeatingToChange + " Stühle entfernt");
                                } else {
                                    print(constantMessages.get(messageCodes.REMOVESEATINGSUNAVAILABLE));
                                }
                            }
                            case 3 -> print(constantMessages.get(messageCodes.BACKTOMENU));
                            default -> print(constantMessages.get(messageCodes.NOOPTION));
                        }
                    } while (option > 3);
                    if(isExactlyOne(table.length)) System.out.println("Der Tisch ist jetzt umgeben von "+ table.length+ "Stuhl.");
                    else System.out.println("Der Tisch ist jetzt umgeben von " + table.length + " Stühlen.");
                }
                case 5 -> print(constantMessages.get(messageCodes.BYE));
                default -> print(constantMessages.get(messageCodes.NOOPTION));
            }
        }while(mainSelection != 5);
    }
    /*
    Functions:
        1. Adding chairs
        2. Removing chairs
        3. Get seating options
        4. Registration
        5. Printing
        6. Menu selection
        7. Helper
     */
    /* ============================================================================================================= */
    // 1. ADD CHAIRS
    public static boolean[] addSeatingToTable(boolean[] table, int amountOfSeating, int indexToInsertSeating, HashMap<Integer, Integer> groupsOnTable){
        int newLength = table.length + amountOfSeating;
        var newTable = createBooleanArrayWithTrueValues(newLength);
        int index = indexToInsertSeating;
        if(indexToInsertSeating == -1){
            index = getIndexBetweenGuestsWhenFull(groupsOnTable) - 1;
        }
        for(int i = 0; i < table.length; i++){
            if(i >= index){
                newTable[i + amountOfSeating] = table[i];
            }else{
                newTable[i] = table[i];
            }
        }
        return newTable;
    }


    public static HashMap<Integer, Integer> addSeatingToMap(HashMap<Integer, Integer> groupsOnTable, int amountOfSeating, int indexToInsertSeating ){
        int indexToInsertSeatingInHashMap = indexToInsertSeating + 1; //+1 due to table-number
        var newHashMap = new HashMap<Integer, Integer>();
        Queue<Integer> valuesOld = new LinkedList<>();
        groupsOnTable.forEach((key, value) -> valuesOld.add(value));
        boolean justOneGuestGroup = valuesOld.stream().distinct().count() <= 1;

        if(justOneGuestGroup){
            insertAndMoveHashMap(groupsOnTable, amountOfSeating, 1, newHashMap, valuesOld);
        }else{
            if(indexToInsertSeating == -1){
                int insertIndex = getIndexBetweenGuestsWhenFull(groupsOnTable);
                insertAndMoveHashMap(groupsOnTable, amountOfSeating, insertIndex, newHashMap, valuesOld);
            }else{
                insertAndMoveHashMap(groupsOnTable, amountOfSeating, indexToInsertSeatingInHashMap, newHashMap, valuesOld);
            }
        }

        return newHashMap;
    }

        private static int getIndexBetweenGuestsWhenFull(HashMap<Integer, Integer> groupsOnTable){
            int firstInstance = groupsOnTable.get(1);
            for(int i = 1; i < groupsOnTable.size() + 1; i++){
                if(firstInstance != groupsOnTable.get(i)){
                    return i;
                }
            }
            return -1;
        }
        //Bug here
        private static void insertAndMoveHashMap(HashMap<Integer, Integer> groupsOnTable, int amountOfSeating, int indexToInsertSeating, HashMap<Integer, Integer> newHashMap,Queue<Integer> valuesFromOldMap ){
            int range = indexToInsertSeating + amountOfSeating;
            for(int i = 1; i < groupsOnTable.size() + amountOfSeating + 1; i++){
                if( i >= indexToInsertSeating  && i < range ){
                    newHashMap.put(i, 0);
                }else{
                    newHashMap.put(i, valuesFromOldMap.remove());
                }
            }
        }

    /* ============================================================================================================= */
    // 2. REMOVE CHAIRS
    public static boolean[] removeSeatingFromTable(boolean[]table, int amountToRemove, ArrayList<Integer> list){
        int newLength = table.length - amountToRemove;
        int index = 0;
        boolean[] newTable = createBooleanArrayWithTrueValues(newLength);

        for(int i = 0; i < table.length; i++){
            if(list.contains(i)){
                continue;
            }
            newTable[ index++ ] = table[i];
        }

        return newTable;
    }
    public static HashMap<Integer, Integer> removeSeatingFromMap(HashMap<Integer, Integer> groupsOnTable, int amountToRemove, ArrayList<Integer> list){
        HashMap<Integer, Integer> newMap = new HashMap<>();
        Queue<Integer> valuesOld = new LinkedList<>();
        boolean noGuests = getKeysByValue(groupsOnTable, 0).size() == groupsOnTable.size();
        if(noGuests){
            for(int i = 1; i < groupsOnTable.size() - amountToRemove + 1; i++){
                newMap.put(i, 0);
            }
            return newMap;
        }
        groupsOnTable.forEach((key, value) ->{
            if(!list.contains(key -1)) {
                valuesOld.add(value);
            }
        });
        for(int i = 1; i < groupsOnTable.size() - amountToRemove + 1; i++){
                newMap.put(i, valuesOld.remove());
        }
        return newMap;
    }
    public static boolean ableToRemoveAmountFromTable(boolean[] table, int amountToRemove){
        if(areAllTrue(table)) return true;
        int count = 0;
        for (boolean b : table) {
            if (b) count++;
        }
        return count >= amountToRemove;
    }

    /* ============================================================================================================= */
    // 3. GET SEATING OPTIONS
    public static int getFirstPossibleSeatPlace(boolean[] table){
        for(int i = 1; i < table.length; i++){
            if(table[i]) return i;
        }
        return -1;
    }


    public static ArrayList<ArrayList<Integer>> getSeatingOptions(boolean[] table){  //seems to work
        ArrayList<ArrayList<Integer>> options = new ArrayList<>();
        int tableLength = table.length;
        var option = new ArrayList<Integer>();

        if(areAllTrue(table)) {
            for(int i = 0; i < tableLength; i++){
                option.add(i);
            }
            options.add(option);
            return options;
        }
        int startingPoint = 0;
        if( !table[0] ){
            startingPoint = getFirstPossibleSeatPlace(table);
            if(startingPoint == -1) return new ArrayList<>();
        }

        for(int i = startingPoint; i < (tableLength * 2) ; i++ ){
            if(table[i % tableLength]){
                option.add(i % tableLength);
            }
            if(!option.isEmpty()){
                if(!(table[i % tableLength])){
                        options.add(option);
                        option = new ArrayList<>();  //option.clear() not working
                }
            }

        }
        return options;
    }

    public static ArrayList<Integer> getBestSeatingOption(int numberOfGuests, boolean[] table){
        ArrayList<ArrayList<Integer>> options = getSeatingOptions(table);
        if(options.size() == 0 || noOptionIsEnoughSpace(numberOfGuests, options)) return new ArrayList<>(); //return empty list and function to prevent Overflow
        if(options.size() == 1){
            return options.get(0); //return only list
        }

        ArrayList<Integer> bestOption = new ArrayList<>();
        int bestOptionLength = table.length + 1;  //biggest value to start
        for (var option : options) {
            option.forEach(System.out::print);
            int length = option.size();
            if(length >= numberOfGuests){
                var pufferLength = option.size();
                if(pufferLength < bestOptionLength){
                    bestOption = new ArrayList<>(option);
                    bestOptionLength = pufferLength;
                }
            }
        }
        return bestOption;
    }
        private static boolean noOptionIsEnoughSpace(int numberOfGuests, ArrayList<ArrayList<Integer>> options){
            for(var option: options){
                if(option.size() >= numberOfGuests) return false;
            }
            return true;
        }

    /* ============================================================================================================= */
    // 4. REGISTRATION
    public static boolean registerGuests(int numberOfGuests, boolean[] table, HashMap<Integer,Integer> groupsOnTable, int id, ArrayList<String> bookOutOptions){
        var bestOption = getBestSeatingOption(numberOfGuests, table);
        if(bestOption.isEmpty() || bestOption.get(0) == -1) return false;
        bookOutOptions.add("Gruppe-"+id);
        for(int i = 0; i < numberOfGuests; i++){
            int index = bestOption.get(i);
            table[index] = false;
            groupsOnTable.put(index + 1, id); //+1 because first key is 1 not 0
        }
        return true;
    }

    public static void unregisterGuests(String groupIdToRemove, boolean[] table, HashMap<Integer, Integer> groupsOnTable, ArrayList<String> bookOutOptions){
        int id = Integer.parseInt(groupIdToRemove.substring(groupIdToRemove.lastIndexOf("-")+1));
        var set = getKeysByValue(groupsOnTable,id);
        set.forEach(entry -> {
            table[entry - 1] = true; // -1 because tableNumber starts with 1
            groupsOnTable.put(entry, 0);
        });
        bookOutOptions.remove(groupIdToRemove);
        int amountOfPeople = set.size();
        if(isExactlyOne(amountOfPeople)) System.out.println(groupIdToRemove+" mit "+amountOfPeople+" Person wurde ausgebucht");
        else System.out.println(groupIdToRemove+" mit "+amountOfPeople+" Personen wurde ausgebucht");
        System.out.println("Folgende Tische sind wieder frei:");
        set.forEach(entry -> System.out.print(entry+ " "));
        System.out.println();
    }

    /* ============================================================================================================= */
    // 5. PRINTING

    public static void printSeatings(boolean[] table, HashMap<Integer,Integer> map){
        StringBuilder s = new StringBuilder();
        int count = 0;
        System.out.println("Folgende Sitzplätze sind belegt: ");
        for(int i = 1; i < table.length + 1; i++){
            if(table[i-1]){
                s.append(" | Platz-").append(i).append(": ").append(map.get(i));
            }else{
                s.append(" | Platz-").append(i).append(": ").append(map.get(i));
                count++;
            }
        }
        int space = table.length - count;
        System.out.println(s);
        if(isExactlyOne(count)) System.out.println(count + " Person isst gerade.");
        else System.out.println(count + " Personen essen gerade.");
        if(isExactlyOne(space)) System.out.println(space+" Platz ist noch frei.");
        else System.out.println(space+" Plätze sind noch frei.");
    }

    /* ============================================================================================================= */
    // 6. MENU SELECTION
    public static int selectOptions(ArrayList<String> options){
        System.out.println("----------------------------");
        for(int i = 0; i < options.size(); i++){
            System.out.println(i+1 +" - "+options.get(i));
        }
        System.out.println("----------------------------");
        return selectNumber();
    }

    public static int selectNumber(){
        int selection = -1;
        Scanner sc = new Scanner(System.in);

        do{
            try {
                System.out.print("Eingabe: ");
                selection = sc.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Nur Ganzzahlen sind erlaubt, bitte wähle erneut.");
            }
            sc.nextLine(); //clear buffer
        }while(selection < 0);

        System.out.println("===========================");
        return selection;
    }

    /* ============================================================================================================= */
    // 7. HELPER
    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static boolean[] createBooleanArrayWithTrueValues(int length){
        boolean[] newTable = new boolean[length];
        Arrays.fill(newTable, Boolean.TRUE);
        return newTable;
    }

    public static HashMap<Integer, Integer> createIntegerHashMapWithSeating(int length){
        HashMap<Integer, Integer> newMap = new HashMap<>();
        for(int i = 1; i < length + 1; i++){
            newMap.put(i, 0);
        }
        return newMap;
    }
    public static void print(String message){
        System.out.println(message);
    }
    public static boolean isExactlyOne(int amount){return amount == 1; }

    private static boolean areAllTrue(boolean[] array){
        for(boolean item: array) if(!item) return false;
        return true;
    }
    private static boolean areAllFalse(boolean[] array){
        for(boolean item: array) if(item) return false;
        return true;
    }

}
