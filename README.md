Readme del progetto originale fornito: [qua](./README-base.md)

# Passaggi generali

Partendo da un certo stato
1. Ricevi stato dal server, se tocca a te:
2. **Creazione albero** degli stati possibili entro N turni, in base alle mosse
3. Si parte con **algoritmo min-max** con $\alpha-\beta$ pruning o quel che è
    * Arrivati ai nodi foglia, si calcola il valore dallo stato in base alla **funzione euristica**
5. Decisa la mossa, la invii al server


## Creazione albero

Partendo da stato A, e decidendo profondità massima N, nel turno nostro:
1. **Elenco mosse possibili** a partire da stato A nel turno nostro
    * Per ogni pedina, elencare mosse che può fare
2. Per ogni mossa possibile, si crea sottoalbero, cambiando il turno da analizzare e cambiando lo stato in base alla mossa
3. Ripeti fino a profondità N o mossa conclusiva

## Funzione euristica

### Appunti vari

* Molto più importante posizione re che pedine prese, in ambo i casi
* Avere una pedina avversaria adiacente mette a rischio

Bianchi
* Bianco non deve lasciare re scoperto muovendosi
* Meglio muovere il re solo quando c'è un basso numero di neri

Neri
* Se il nero vede il re scoperto, è saggio muovercisi di fianco


### Caso Bianchi

Scopo: re deve scappare

Opzione 1:
* Se è in una casella dove potenzialmente potrebbe scappare: conta pedine di intralcio, -1 ciascuna
* Se non lo è: conta pedine di intralcio per andare su una di quelle, -1 ciascuna
* -2 per ogni pedina bianca persa, -inf per il re perso

### Caso neri

Scopo: inizialmente mangiare, poi bloccare

Opzione 1: 
* Sotto N bianchi mangiati: mangiare +2 ogni bianco mangiato
* Sopra N bianchi mangiati: +1 ogni bianco mangiato
* In generale: +1 ogni pedina tra re e caselle fuga (stessa logica bianchi)

# Elementi

## Cose da fare

* Classe per elencare mosse possibili da uno stato per un colore
    * Partendo da stato, ogni mossa *legale* che può fare una pedina
    * Interfaccia: ✔️ [*IListActions*](./Tablut/src/it/unibo/ai/didattica/competition/tablut/droptablut/interfaces/IListActions.java)
* Classe per ottenere nuovo stato da stato + mossa
    * Interfaccia: ✔️ [*IApplyAction*](./Tablut/src/it/unibo/ai/didattica/competition/tablut/droptablut/interfaces/IApplyAction.java)
* Funzione per generare albero
    * Interfaccia: ✔️ [*ICreateTree*](./Tablut/src/it/unibo/ai/didattica/competition/tablut/droptablut/interfaces/ICreateTree.java)
* Implementazione min-max (già esistente? controllare libreria AIMA)
    * C'è in libreria AIMA (classe [aima.core.search.adversarial.*AlphaBetaSearch*](https://github.com/aimacode/aima-java/blob/AIMA3e/aima-core/src/main/java/aima/core/search/adversarial/AlphaBetaSearch.java)), da capire come funziona
    * Interfaccia: [*IMinMax*](./Tablut/src/it/unibo/ai/didattica/competition/tablut/droptablut/interfaces/IMinMax.java)
* Funzione euristica: punteggio di uno stato
    * Interfaccia: [*IHeuristic*](./Tablut/src/it/unibo/ai/didattica/competition/tablut/droptablut/interfaces/IHeuristic.java)

## Cose già fornite

* Rappresentazione stato
* Rappresentazione mosse
* Comunicazione con server
* Validazione mosse

## Possibili ottimizzazioni

* Generare albero solo per rami necessari, quindi dentro alpha-beta
* Tracciare Action durante alpha-beta per evitare loop finale
    * bestValue oggetto con double e action

## Migliorie euristica

* Aggiungere parametro "neri vicino al re"
* ✔️ Aumentare di molto peso delle vie libere del re