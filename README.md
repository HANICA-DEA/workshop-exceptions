# Introductie
Deze oefening is deel van de DEA Course aan de Hogeschool Arnhem/Nijmegen. Doel is het bekend raken met exceptions in het bijzonder bij het schrijven van unit tests voor code die exceptions kan gooien.

# Oefeningen
In deze oefening ga je verder met de FizzBuzz kata die je al in twee stappen hebt ontwikkeld:
1. De eerste keer was het schrijven van de methode om Java-kennis wat op te halen.
2. De tweede keer was het schrijven van de methode in stapjes, ondersteund door unit tests en gebruik makend van Maven. Een mogelijke uitwerking hiervan vind je op https://github.com/HANICA-DEA/voorbereiding-junit/tree/uitwerking. 
3. Deze derde keer leer je hoe je om moet gaan met randgevallen zoals de aanroep van de methode met een negatief getal of een te hoog getal.

## Voorbereiding
Ga naar https://github.com/HANICA-DEA/voorbereiding-junit/tree/uitwerking. Download de zip of gebruik git om het gehele project te downloaden (```git clone```) naar je computer en open dit project in IntelliJ.

## Oefening 1: Red, Green
Run de bestaande tests vanuit IntelliJ en check dat ze groen zijn, doe dit daarna nog een keer vanuit de command-line met Maven. Voeg nu een nieuwe test toe die een lege String terug geeft als de input kleiner is dan 0, bijvoorbeeld -1:

```java
@Test
void executeWithNegativeIntTest() {
    // Arrange

    // Act
    var testValue = sut.execute(-1);

    // Assert
    Assertions.assertEquals("", testValue);
}
```

Run de test, die wordt rood. Pas vervolgens de execute methode aan van de ```FizzBuzzExecutor``` zodat de nieuwe test groen wordt. 

## Oefening 2: Refactor
Alle asserts (de 3e A van de AAA-aanpak) zitten als static methode in de ```Assertions class```. Zorg voor een [static import](https://www.geeksforgeeks.org/static-import-java/) van ```Assertions.*``` en verwijder aanroepen van ```Assertions.assertEquals``` naar ```assertEquals```. Dit is een refactoring van de test code, run de tests opnieuw en check dat ze nog steeds groen zijn. Je hebt nu 1 cyclus doorlopen van de TDD-cyclus (red, green, refactor). 

## Oefening 3: Een eigen exception verwachten in een testcase (Red)
Wijzig de laatst toegevoegde testcase, in plaats van een lege String te returnen verwachten we nu dat in dat geval een exception van het type ```NegativeInputException``` gegooid wordt:

```java
@Test
void executeWithNegativeIntTest() {
    // Arrange

    try {
        var testValue = sut.execute(-1);
        fail();
    } catch(NegativeInputException nie) {
        // Do nothing
    }    
}
```

Probeer de unit test te runnen, je zult zien dat het niet compileert omdat de class `NegativeInputException` nog niet bestaat. Dit gaan we rechtzetten in de volgende stap. Maar er is meer aan de hand, er staat namelijk geen assert in! Op zich werkt het (sort of...), want de test case faalt als er geen exception optreedt en slaagt als hij wel optreedt. Misschien is deze laatste zin wel begrijpelijk, maar de code moet je waarschijnlijk meerdere keren lezen om te snappen wat er gebeurt. We gaan in deze TDD-cyclus eerst naar groen en kijken later of dit mooier kan door te refactoren. 

## Oefening 4: Een eigen exception maken (Red)
Voeg in `src/main/java` aan het bestaande package een nieuwe class toe `NegativeInputException` en laat deze overerven van de class `Exception`. Deze superklasse is een zogenaamde _"checked exception"_, dat betekent dat als je die ergens gooit (hiervoor gebruik je het keyword ```throw```) je aan alle aanroepers van die functie moet laten weten dat die exception mogelijk kan optreden (hiervoor gebruik je het keyword `throws`), de aanroeper moet vervolgens kiezen of die exception gevangen hiervoor gebruik je het keyword `try` en `catch` gaat worden en/of doorgegooid naar een volgende aanroeper.

Run nu de unit test uit oefening 3. Het zal nog niet compileren want in de execute methode wordt er nog geen exception gegooid. 

## Oefening 5: Een eigen exception gooien (Green)
Pas de implementatie van de execute methode aan dat er een nieuwe instantie van `NegativeInputException` gecreëerd en gegooid wordt als de input kleiner is dan 0. Omdat het een checked exception is moet je ook de methode-declaratie aanpassen:

```java
public String execute(int i) throws NegativeInputException {
```

Zorg dat in de overige test cases een try-catch blok om de aanroep van execute staat en plaats in de catch een aanroep van fail(). Zet ook een try-catch blok om de execute aanroep in de `FizzBuzzRunner`. Run nu de unit test nog een keer en check dat hij groen is. 

## Oefening 6: JUnit assertTrows gebruiken (Refactor)
De test uit oefening 3 heeft zo zijn beperkingen, gelukkig heeft JUnit een handige assertThrows die dat oplost. Pas de unit test aan:

```java
@Test
void executeWithNegativeIntTest() {
    // Arrange

    // Assert
    assertThrows(NegativeInputException.class, () -> {
        // Act
        var testValue = sut.execute(-1);
    }); 
}
```

## Oefening 7: Een eigen exception gooien (Refactor)
De eigengemaakte NegativeInputException heeft nu ```Exception``` als superklasse. Je hebt gezien dat je een aantal aanpassingen moet maken om de compiler tevreden te krijgen. Wijzig nu de ```NegativeInputException``` en kies ```RuntimeException``` als superklasse. Run de unit test opnieuw, die zijn nog steeds groen. Het verschil tussen een ```RuntimeException``` (unchecked) en ```Exception``` (checked) wordt in deze [bron](https://www.java67.com/2012/12/difference-between-runtimeexception-and-checked-exception.html) netjes uitgelegd. Een quote uit dat artikel:

```text
"If a method is likely to fail and the chances of failure are more than 50% it should throw Checked Exception to ensure alternate processing in case it failed. Another thought is that programming errors should be unchecked and derived from RuntimeException e.g. java.lang.NullPointerException". 
```

De kans dat een andere ontwikkelaar onze methode aanroept zien we in deze workshop als een programmeerfout, ```RuntimeException``` is dan een goede keus. In dat geval mag de aanpassing uit oefening 5 ongedaan gemaakt worden. 

## Oefening 8: Een eigen exception verwachten en eigenschappen checken in een testcase (Red, Green)
We hebben tot nu toe vooral het type gecheckt maar wat als je specifiek wil kijken of de exception message (of een andere eigenschap) voldoet aan specifieke eisen? 

Wijzig de testcase:

```java
@Test
void executeWithNegativeIntTest() {
    // Arrange

    // Assert
    Exception ex = assertThrows(NegativeInputException.class, () -> {
        // Act
        var testValue = sut.execute(-1);
    }); 
    
    // Assert
    assertEquals("Input should be a positive integer", ex.getMessage());  
}
```

Run de testcase, deze faalt want de exception message is leeg. 

Voeg aan `NegativeInputException` een constructor toe die een `String message` als parameter meekrijgt en roep in de constructor de constructor van de superklasse aan en geef daar de waarde van message aan mee. Wijzig in de execute methode de creatie van de exception door de String "Input should be a positive integer" mee te geven. Run de testcase opnieuw, die wordt groen.

## Afronding

In deze workshop heb je kennis gemaakt met:
* assertThrows
* het maken van eigen exceptions met verschillende superklassen (checked en unchecked)
* de keywords `try, catch, throw, throws`
* het checken van eventuele eigenschappen van een exception, bijvoorbeeld de message



