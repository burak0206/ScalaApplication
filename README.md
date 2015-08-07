# Projenin Amacı

Bu proje, uygulama açısından anlamlı komutları konsoldan alarak gerçekleştirir.

# Projenin Çalıştırılması

Öncelikle MongoDB ve sbt'nin yüklü olması gerekiyor. Git kullanarak ya da sıkıştırılmış halini indirerek projeye erişebilirsiniz.
Terminalden projenin altdizinine giderek ```sbt "run-main akka.Main com.console.app.Application``` komutu ile projeyi çalıştırabilirsiniz. Git kullanmak isteyenler aşağıdaki komutları sırayla uygulayabilirler:

1. ```git clone https://github.com/burak0206/ScalaApplication.git```

2. ```cd ScalaApplication```

3. ```sbt "run-main akka.Main com.console.app.Application"```

# Başlangıç Ekranı

```Commands Example:```

```1. load filename1 filename2 ...```

```2. findByName name```

```3. add filename1 filename2 ...```

```4. drop```

```5. exit```

```6. help```

```7. loadExtFile /Users/burakdagli/Desktop/contacts.xml ...```

```8. addExtFile /Users/burakdagli/Desktop/contacts.xml ...```

```Please, You enter command:```

Not: 5 ve 6 dışındaki komutlar MongoDB ile çalışmaktadır. Eğer MongoDB çalışmıyorsa hata mesajı ile birlikte proje durdurulur.

Örnek:

`drop`

`com.mongodb.MongoTimeoutException: ... `

`Mongo is not running!`

`Application is closing because there is an exception`

# Komutlar

Proje `src/main/resources` altdizini içerisinde 3 tane xml dosyası bulunmaktadır. Testleri kolaylaştırması için bu dosyalar eklenmiştir. Dosyaların adları `contacts.xml`, `contacts2.xml` ve `contacts22.xml`. Aralarında `contacts22.xml` adlı dosya hata içermektedir. Dosyanın eklenmesindeki amaç bu hata durumunda uygulamanın verdiği yanıtı göstermektir.

* `exit`

Uygulamayı sonlandırır.

* `help`

Uygulamadaki geçerli komutları ve parametrelerini gösterir.

* `drop`

MongoDB içerisinde oluşturulan collection drop edilir. Eğer MongoDB i çalışmıyorsa hata kodu döndürülür ve uygulama sonlandırılır.

* `load filename1 filename2 ...` ve `loadExtFile /Users/burakdagli/Desktop/contacts.xml ...`

`load` komutu n adet dosya ismini parametre alarak çalışır. Bu dosyalar `src/main/resources` dizini altında olmak zorundadır. Dosyalardan en az biri yoksa, dosyalardan en az biri hata taşıyorlarsa ya da MongoDB  çalışmıyorsa hata kodu döndürülür ve uygulama sonlandırılır. Test için `contacts.xml`, `contacts2.xml` ve `contacts22.xml` adlı dosyalar eklenmiş olup `contacts22.xml` hatalar barındırmaktadır. Dosyalardan en az biri yoksa, dosyalardan en az biri hata taşıyorlarsa ya da MongoDB  çalışmıyorsa hata kodu döndürülür ve uygulama sonlandırılır. Komut çağrıldıktan sonra önceki collection drop edilir. Ardından yeni veriler eklenir.

`loadExtFile` komutu n adet dosya yolunu parametre alarak çalışır. Örneğin:
`loadExtFile /Users/burakdagli/Desktop/contacts.xml /Users/burakdagli/Desktop/contacts2.xml`
Dosyalardan en az biri yoksa, dosyalardan en az biri hata taşıyorlarsa ya da MongoDB  çalışmıyorsa hata kodu döndürülür ve uygulama sonlandırılır. Komut çağrıldıktan sonra önceki collection drop edilir. Ardından yeni veriler eklenir.

* `add filename1 filename2 ...` ve `addExtFile /Users/burakdagli/Desktop/contacts.xml ...`

`add` ve `addExtFile` komutları da `load`, `loadExtFile` arasındaki benzerliği taşır. Onlardan farkları collection drop edilmez, böylelikle eski veriler silinmez. Dosyalardan en az biri yoksa, dosyalardan en az biri hata taşıyorlarsa ya da MongoDB  çalışmıyorsa hata kodu döndürülür ve uygulama sonlandırılır.

* `findByName name`
 
Bu komut kişilerin birden fazla ismi olabileceği düşünülerek tasarlandı. Örneğin: `findByName Nidal Aras`

`{ "_id" : { "$oid" : "55c3b8e706595b0392b8bc3d"} , "lastName" : "Dağlı" , "name" : "Nidal Aras" , "phones" : [ "+90 505 911 11 11"]}`

 Sorgu sonucu boş ise `Contact is not found!` mesajı döner.
 MongoDB  çalışmıyorsa hata kodu döndürülür ve uygulama sonlandırılır.


Eğer bunların dışında ya da parametre formatları dışında komutlar girilirse hata mesajı dönülür ve yeni komut istenir.

`Please, You enter command:`

`close`

`Wrong or Missing Command! You can enter help`

# Index

MongoDB'ye yapılan arama sorguları `name` alanı ile sınırlandırılması istenmiştir. Ayrıca `name` ve `lastName` ikilisinin tekil olması istenmiştir. Bu iki durumu kapsayacak bir şekilde aşağıda tanımlanan index eklenmiştir:

`coll.createIndex(MongoDBObject("name" -> 1, "lastName" -> 1), MongoDBObject("unique" -> true))`
