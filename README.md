# Q-AcademicoIFF

A Web Scrapping Application developed in Java using JavaFX with the purpose of extracting notes from an academic system belonging to the Instituto Federal Fluimense.

![](https://github.com/C0lliNN/Q-AcademicoIFF/blob/master/screenshots/authetication.png)

## What is the Q-Academico IFF ?

[**Q-Acadêmico IFF**](https://academico.iff.edu.br/qacademico/index.asp?t=1001) is a system developed by [**Qualidata**](http://www2.qualidata.com.br/) for the [**Instituto Federal Fluminense (IFF)**](http://portal1.iff.edu.br/) with the primary purpose of allowing teachers to release grades and students to view them. However, this system is very old and does not look attractive. In this sense, this application was developed so that the students of the institution can view their grades in a much more pleasant and attractive way.

## Requirements

* JDK 8+
* Internet Connection

## How it works

The Application use the [**HtmlUnit**](https://www.seleniumhq.org/projects/webdriver/) which is a web browser controlled by the Java programming language. With this feature, each time the user requests login to the application, this broswer opens in the background (without the user seeing it). Then the browser logs in to the site with the data provided by the user in the application, enters the newsletter, extracts and treats the data (the grades). Finally, the newsletter will be displayed to the user.

![](https://github.com/C0lliNN/Q-AcademicoIFF/blob/master/screenshots/dashboard.png)

