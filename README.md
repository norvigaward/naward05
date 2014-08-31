Song mentioning detection on the Common Crawl dataset
============

#### Abstract

We investigated the possibility of automatically developing lists of popular songs for different countries by detecting song mentionings in the Common Crawl dataset.
We found that that it is possible to efficiently determine the country of a web page and that is is possible to efficiently find mentionings of a large number of songs
titles (5.7 million) within these web pages. We find however that our current implementation is not sufficient as the amount of false positive song mention detections is too high,
but show that on a smaller set of songs, particular the Dutch top2000, the approach produces interesting reshuffeling of this list for different countries.

#### Introduction


#### Background

The research performed for the Norvig Award has resulted in a paper that will be published as part of (todo, study tour, link naar paper).


#### Overview Method



#### Results

Below is the generated list of the top 15 most mentioned songs for a number of different countries.

After that, we give the combined top list of top 15 most mentioned songs and the top 15 of the original top2000.  

NL

Song					| Artis            	| #Occurences
------------------------|-------------------|--------
Royals					| Lorde				|	9955
Roar					| Katy Perry		|	9863
Pompeii					| Bastille			|	1758
Don't Dream It's Over   | Crowded House		|	1330
Get Lucky				| Daft Punk			|	334
Diamonds				| Rihanna			|	304
Let Her Go				| Passenger			|	203
Wake Me Up				| Avicii			|	202
One						| Metallica			|	185
Kiss					| Prince			|	154
Alone					| Heart				|	147
All Of Me				| John Legend		|	137
Story Of My Life		| One Direction		|	137
Losing My Religion		| REM				|	135
Thriller				| Michael Jackson 	|	104

GB

Song					| Artis            	| #Occurences
------------------------|-------------------|--------
Royals          |Lorde          |	2739
Dreams          |Fleetwood Mac  |	2322
Roar            |Katy Perry     |	552
Alone           |Heart          |	225
Help            |The Beatles    |	217
Try             |Pink           |	185
Imagine         |John Lennon    |	170
Thriller        |Michael Jackson|	163
1999            |Prince         |	140
Hey Jude        |The Beatles    |	134
We Will Rock You|Queen          |	134
Get Lucky       |Daft Punk      |	131
Mamma Mia       |ABBA           |	126
She Loves You   |The Beatles    |	125
Biko            |Peter Gabriel  |	123

US

Song					| Artis            	| #Occurences
------------------------|-------------------|--------
Try                 	|Pink         		|	11867
Gangnam Style       	|Psy          		|	7670
Diamonds            	|Rihanna      		|	6509
Blurred Lines       	|Robin Thicke 		|	5840
Ho Hey              	|The Lumineers		|	5735
Locked Out Of Heaven	|Bruno Mars   		|	5377
I Won't Give Up			|Jason Mraz			|	5371
Alone					|Heart				|	4808
Purple Rain				|Prince				|	4509
The Unforgettable Fire	|U2					|	4388
Gloria 					|Them       		|	4137
Royals 					|Lorde      		|	3882
Vertigo					|U2         		|	3855
Roar   					|Katy Perry 		|	3613
Time   					|Pink Floyd 		|	3579

Combined list of all countries.

Song					| Artis            	| #Occurences
------------------------|-------------------|--------
Royals              		|Lorde        		|	19016
Roar                		|Katy Perry   		|	15306
Try                 		|Pink         		|	12402
Gangnam Style       		|Psy          		|	8396
Diamonds            		|Rihanna      		|	7247
Blurred Lines       		|Robin Thicke 		|	6272
Alone               		|Heart        		|	5855
Ho Hey              		|The Lumineers		|	5845
Locked Out Of Heaven		|Bruno Mars			|	5503
I Won't Give Up				|Jason Mraz			|	5439
Purple Rain           		|Prince  			|	4698
Pompeii               		|Bastille			|	4573
Gloria                		|Them    			|	4466
The Unforgettable Fire		|U2      			|	4463
Someone Like You      		|Adele   			|	4394


Original Top2000

Song					| Artis            	
------------------------|-------------------
Bohemian Rhapsody		       |  Queen
Hotel California               |  Eagles
Stairway To Heaven             |  Led Zeppelin
Child In Time                  |  Deep Purple
Avond                          |  Boudewijn de Groot
Wish You Were Here             |  Pink Floyd
The River                      |  Bruce Springsteen
Comfortably Numb               |  Pink Floyd
Nothing Else Matters           |  Metallica
Shine On You Crazy Diamond     |  Pink Floyd
Brothers In Arms               |  Dire Straits
Clocks                         |  Coldplay
November Rain                  |  Guns N' Roses
One                            |  Metallica
Viva La Vida                   |  Coldplay

The complete set of result can be found in [this .csv file](results.csv)

#### Discussion

Because a large majority of the webpages (~80%) and also the most occurences are found in pages from the US. The US list has a large impact on the combined list of songs.
