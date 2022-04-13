Readme del progetto originale fornito: [qua](./README-base.md)

# Passaggi generali

Partendo da un certo stato
1. Ricevi mossa dell'avversario dal server e aggiorni stato interno.
2. **Creazione albero** degli stati possibili entro N turni, in base alle mosse
3. Si parte con **algoritmo min-max** con $\alpha-\beta$ pruning o quel che è
4. Arrivati ai nodi foglia, si calcola il valore dallo stato in base alla **funzione euristica**
5. Decisa la mossa, la invii al server


## Creazione albero

Partendo da stato A, e decidendo profondità massima N, nel turno nostro:
1. **Elenco mosse possibili** a partire da stato A nel turno nostro
    * Per ogni pedina, elencare mosse che può fare
2. Per ogni mossa possibile, si crea sottoalbero, cambiando il turno da analizzare e cambiando lo stato in base alla mossa
3. Ripeti fino a profondità N o mossa conclusiva
