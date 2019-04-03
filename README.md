# Q-AcademicoIFF

Uma aplicação feita em java usando javafx com objetivo de colocar os dados de um sistema acadêmico online em uma aplicação desktop mais interativa.

![](https://i.imgur.com/diHjumb.png)

## O que é o Q-Acadêmico IFF ?

[**Q-Acadêmico IFF**](https://academico.iff.edu.br/qacademico/index.asp?t=1001) é um sistema desenvolvido pela [**Qualidata**](http://www2.qualidata.com.br/) para o [**Instituto Federal Fluminense (IFF)**](http://portal1.iff.edu.br/) com objetivo principal de permitir que professores lancem notas e os alunos possa visualizar. Porém, esse sistema já bem antigo e não possui uma aparência atrativa. Por isso decide fazer essa aplicação usando Java que é destinada para alunos da instituição. Com ela, é possível para os alunos visualizar suas notas de uma maneira bem mais atrativa.

## Como Funciona

A aplicação usa o [**Selenium WebDriver**](https://www.seleniumhq.org/projects/webdriver/) que é um driver disponível para ser usado em várias linguagens de programação, que permite controlar navegadores WEB via programação. Com esse driver, cada vez que o usuário solicita login na aplicação, um navegador no web (nesse projeto estou usando Google Chrome) é aberto no background (sem que o usuário veja). Em seguida, o navegador faz login no site com os dados fornecidos pelo usuário na aplicação, entra no boletim e retorna o código fonte (HTML) do site. O código fonte é tratado com a finalidade de retirar os dados e mostrar para o usuário da aplicação.

![](https://i.imgur.com/HWgXRgn.png)

## Como Utilizar

Essa aplicação apenas pode ser usada por alunos do IFF. No link abaixo é possível baixar o arquivo executável da aplicação

Link: [**Q-Acadêmico IFF**](https://academico.iff.edu.br/qacademico/index.asp?t=1001)
