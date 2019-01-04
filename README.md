[![Build Status](https://travis-ci.com/ElaadF/Easymow-Scala-M2.svg?token=1HfxJpzgfSyTHsDYM9Kj&branch=master)](https://travis-ci.com/ElaadF/Easymow-Scala-M2)   [![codecov](https://codecov.io/gh/ElaadF/Easymow-Scala-M2/branch/master/graph/badge.svg?token=iYwACpuzjD)](https://codecov.io/gh/ElaadF/Easymow-Scala-M2)


# Easymow-Scala-M2
[Sujet Easymow](https://github.com/upem-scala-2018/course3-typeclasses/blob/master/projet-easymow.pdf)   
Elaad FURREEDAN   
Master 2 - Logiciel   
[Documentation](https://elaadf.github.io/Easymow-Scala-M2)

## Utilisation
lancer le programme :   
```
sbt run
```
lancer les tests :   
```
sbt test
```
lancer les tests avec coverage :
```
clean coverage test coverageReport
```
Une fois le programme lancé, il vous demandera de sélectionner un fichier, un fichier d'exemple est founit et se situe dans
```src/main/resources/input```
Le programme vous afichera la nouvelle position de chaque tondeuse **valide**

Exemple :   
```
$ sbt run
> Select a file : src/main/resources/input
[RESULT] 2019-01-02 21:29:31.840 [main] Easymow$ - (0, 5, West) => (1, 5, North)
[RESULT] 2019-01-02 21:29:31.840 [main] Easymow$ - (2, 5, West) => (2, 5, West)
[RESULT] 2019-01-02 21:29:31.840 [main] Easymow$ - (4, 1, East) => (5, 1, East)
```

## Choix du projet
1. Dans le programme une tondeuse est considérée valide lorsque : 
   * est bien à l'intérieur du terrain
   * si elle ne possède pas la même position avec une autre tondeuse
2. Chaque tondeuse est positionnée au début si elles respectent les règles précédentes 
3. Si une tondeuse rencontre un obstacle (mur ou tondeuse) elle ignorera alors l'action et passera à la suivante

Syntaxe :   
   + Terrain : ```[\t ]*(\d+)[\t ]+(\d+)[\t ]*```
   + Tondeuse : ```^[\t ]*(\d+)[\t ]+(\d+)[\t ]+([newsNEWS]{1})[\t ]*$```
   + Instructions : ```[agdAGD]+```


## Logs
Le programme utilise l'API [log4j](https://logging.apache.org/log4j/2.x/index.html) afin de gérer le système de logger
A chaque execution du programme, un fichier de log retrace alors en détail chaque execution avec les étapes   
Ce fichier ce situe dans ```logs/record.log``` il retrace chaque execution du programme et contient tous les niveaux de logs.
Le projet afficher 4 niveaux de logs actuellement:

| Code   | Int level |
|--------|-----------|
| RESULT | 450       |
| INFO   | 400       |
| WARN   | 300       |
| ERROR  | 200       |

>**Note:** RESULT est un custom log afin d'afficher les résultats liés au déplacement de tondeuse

le fichier de configuration [log4j2.xml](src/main/resources/log4j2.xml) permet de décrire ce qui est affiché ou non sur la console, ce qui est
redirigé vers le fichier ```logs/record.log``` ansi que le format des logs.
### RESULT :
```
[RESULT] 2019-01-02 21:29:31.840 [main] Easymow$ - (0, 5, West) => (1, 5, North)
```
>**Note:** Seuls les logs RESULT et ERROR sont affichés sur la console (```stdout``` et ```stderr``` respectivement)

### INFO :

Etat d'avancement de la tondeuse, coordonnées, direction et instructions
```
[INFO ] 2019-01-02 21:29:31.838 [main] Easymow$ - Position : (0, 5, West) Instructions : DAADAGA
```

### WARN :
Regroupe les informations non dangeureuses pour la vie du programme   
Exemples:   
Un véhicule est hors des limites du terrain
```
[WARN ] 2019-01-02 21:29:31.827 [main] Easymow$ - Location (87,2) is out of bound: vehicles ignored
```
Lorsque plusieurs tondeuses veulent être positionnées à la même coordonnée sur le terrain, elles sont donc non prises en compte
```
[WARN ] 2019-01-02 21:29:31.832 [main] Easymow$ - Conflict between two vehicles location at (2,1): vehicles ignored
```
Lorsqu'une tondeuse essaye d'avancer alors qu'elle fait face à un mur
```
[WARN ] 2019-01-02 21:29:31.839 [main] Easymow$ - Vehicle hit a wall at (0,5): instruction ignored
```
Lorsqu'une tondeuse essaye d'avancer dans la direction d'une autre tondeuse
```
[WARN ] 2019-01-02 21:29:31.839 [main] Easymow$ - Vehicle hit another vehicle at (1,5): instruction ignored
```
### ERROR :
Les erreurs qui empêchent le bon fonctionnement du programme, et donc qui ne peut pas continuer de fonctionner    
Exemples:    

Lorsque le format d'initialisation du terrain n'est pas valide
```
[ERROR] 2019-01-02 21:40:43.355 [main] Easymow$ - Field size definition 5 is incorrect
```
Lorsque l'ouverture du fichier n'a pas fonctionné
```
[ERROR] 2019-01-02 22:00:45.102 [main] Easymow$ - Read File failed : java.io.FileNotFoundException: fe (Aucun fichier ou dossier de ce type)
```
>**Note:** Les ERROR sont afichées sur ```stderr``` sur la console et aussi enregistré dans le fichier de log

## Problèmes rencontrés
### Tester les logs
Afin d'afiner mes tests j'ai essayé d'utiliser l'api **Mockito** afin de tester que mes logger fonctionnaient bien.
Malheureusement je ne suis pas arrivé à mes fins, en effet ma démarche était de surcharger la variable **logger** afin d'y placer un **mock**, mais comme mon objet Log contient des surcharge et des typeclass je ne n'ai pas réussi à instancier un objet Log afin de surcharger logger et ansi pouver tester le fonctionnement de mes logs.

### Caractère immuable du langage
Dû au charactère immuable du langage, il n'est pas naturel d'ajouter des element dans une liste puis de la renvoyer. Pour cela il est necessaire d'utiliser des méthodes récursives afin de modifier ma liste et ansi la garder à jour au sein d'une méthode.
