# Decisões de projeto

## Autômato utilizado

A Atividade 1 foi apresentada como AFND. Para a execução do analisador léxico, o programa aplica transições determinísticas, conforme a especificação do trabalho prático.

## Palavras reservadas

A tabela segue a lista fornecida no enunciado. A comparação não diferencia letras maiúsculas e minúsculas, mas o lexema original é preservado na saída.

Os comandos `read`, `write` e `writeln` são classificados como identificadores, de acordo com a saída de exemplo apresentada pelo professor. Os termos `and`, `or` e `not` recebem o token `Operador lógico`; `mod` recebe `Operador aritmético`.

## Igualdade

O símbolo `=` aparece em duas categorias no enunciado. A implementação o classifica como `Operador relacional`, que corresponde ao seu uso na linguagem.

## Identificadores

Não foi definido limite artificial de tamanho. Um identificador começa com letra ou sublinhado e pode continuar com letras, algarismos ou sublinhado.

## Strings e caracteres

Constantes entre aspas duplas recebem o token `Constante string`. Constantes entre aspas simples recebem `Constante caractere` e devem representar exatamente um caractere.

## Erros léxicos

Cada erro registra lexema, linha, coluna e explicação. O analisador consome a unidade inválida e continua a partir do próximo ponto seguro, permitindo localizar mais de um erro na mesma execução.

## Limitações

Comentários aninhados não são reconhecidos. Constantes não podem atravessar uma quebra de linha. Essas construções não fazem parte da especificação fornecida.
