Song mentioning detection on the Common Crawl dataset
============

Abstract
--------------------------

We investigated the possibility of automatically developing lists of popular songs for different countries by detecting song mentionings in the Common Crawl dataset.
We found that that it is possible to efficiently determine the country of a web page and that is is possible to efficiently find mentionings of a large number of songs
titles (5.7 million) within these web pages. We find however that our current implementation is not sufficient as the amount of false positive song mention detections is too high,
but show that on a smaller set of songs, particular the Dutch top2000, the approach produces interesting reshuffeling of this list for different countries.

Introduction
--------------------------

Making ordered lists of songs is a popular activity performed by both commercial institutes and individuals alike. 
In the Netherlands there is an annual event called the [Top 2000](http://www.radio2.nl/top2000) were people are asked to submit their favorite songs that when combined created a ranked list of the top 2000 greatest songs of all time.

It was this particular event that sparked our interest into how such a list of the "greatest songs of all time" would differentiate when created by people from different countries.
As we are not aware of comparable lists in different countries, we wanted to investigate if it is possible to automatically build an equivalent list for a country by counting the mentionings of songs on web pages originating from a specific countries.

For our research, we developed algorithm that is capable of detecting song mentionings within a given web page and an algorithm that finds the country of origin of a web page, so that by combining these results we gain an ranked
list of songs for a number of different countries.

Background
--------------------------

The research performed for the Norvig Award has resulted in a paper that will be published as part of (todo, study tour, link naar paper).


Overview Method
--------------------------

#### Country detection


#### Song mentioning detection


We first decided that the dataset of songs that we should detect should be as complete as possible. We therefore used the music metadata encyclopedia [MusicBrainz](https://musicbrainz.org/) that contained around 13.5 million recordings at the time.
For the mentioning detection, we decided to use the [LingPipe toolkit](http://alias-i.com/lingpipe/). This provided us with an implementation of the Aho-Corasick string matching algorithm that allowed us to find all song mentionings in a text in a linear amount of time.
To reduce the expected amount of false positives by only detecting song titles, we added the requirement that the artist of the song should be mentioned in the text surrounding the song mentioning.

Unfortunatly, after initial testing we found that this set of songs contains a high number of songs with names and artists that are also often occuring in regular text, e.g. songs named "product" or "contact" that when metioned are not always references to the respective songs.
Due to time limitations, we decided to reduce the amount of false positives by only using the top 2000 of 2013 as input for the algorithm.

#### Combining the detectors.


Results
--------------------------

The result of the experiment is a generated list of songs, ordered on the amount of mentionings, for a large number of countries. As it is not feasible to show the entire result on this page, we show the top 15 most mentioned songs for small number of countries below. The complete result set can be found in [this .csv file](results.csv).


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

For added context, we also show the combined top list of top 15 most mentioned songs and the top 15 of the original top2000.

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


Discussion
--------------------------

Because a large majority of the webpages (~80%) is from the US, and also the most occurences are found in pages from the US. The US list has a large impact on the combined list of songs.

We believe that it is interesting to see that The Beatles scores very high in the GB list, while american artists such as Bruno Mars, Robin Thicke and Pink score high in the US list.

