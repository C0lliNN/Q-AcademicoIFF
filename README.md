# Q-AcademicoIFF

Uma Aplicação Web Scraping feita em Java usando JavaFX com objetivo de extrair notas de um sistema acadêmico pertencente ao Instituto Federal Fluimense.

![](https://i.imgur.com/diHjumb.png)

## O que é o Q-Acadêmico IFF ?

[**Q-Acadêmico IFF**](https://academico.iff.edu.br/qacademico/index.asp?t=1001) é um sistema desenvolvido pela [**Qualidata**](http://www2.qualidata.com.br/) para o [**Instituto Federal Fluminense (IFF)**](http://portal1.iff.edu.br/) com objetivo principal de permitir que professores lancem notas e os alunos as possam visualizar. Porém, esse sistema já bem antigo e não possui uma aparência atrativa. Nesse sentido, essa aplicação foi desenvolvida para que os alunos da instituição possam visualizar suas notas de uma maneira bem mais agradável e atrativa.

## Como Funciona

A aplicação usa o [**HtmlUnit**](https://www.seleniumhq.org/projects/webdriver/) que é um Navegador Web controlado através da linguagem de programação Java. Com esse recurso, cada vez que o usuário solicita login na aplicação, esse broswer é aberto no background (sem que o usuário veja). Em seguida, o navegador faz login no site com os dados fornecidos pelo usuário na aplicação, entra no boletim, extrai e trata os dados (as notas). Em fim, o boletim será exibido ao usuário.

![](https://i.imgur.com/HWgXRgn.png)

