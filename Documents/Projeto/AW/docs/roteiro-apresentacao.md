# Roteiro da apresentação: HemoVida

Orador: **Bruno**, em todos os trechos. Tempo total: cerca de 10 minutos.

## Antes de começar (ensaio)

- `backend/.env` preenchido (veja o README). As senhas de `admin` e `atendente` são as que estão nele.
- Os dados de demonstração são recarregados a cada início. Faça o ensaio, feche tudo e **suba de novo** antes de apresentar, para o banco voltar ao estado inicial.
- Deixe abertas em abas: o GitHub do projeto e, depois de subir, o Swagger.

## 1. Subir o sistema (Bruno)

- Dê dois cliques em `iniciar.bat`. Abrem duas janelas: backend e frontend.
- Fale: Java 21 com Spring Boot e H2 em memória, React com Vite. Nada para instalar além de Java e Node.
- Espere o backend mostrar `Started HemovidaApplication`. Abra http://localhost:5173.

## 2. Login (Bruno)

- Entre como `admin`.
- Mostre que um login errado dá sempre "Usuário ou senha inválidos", sem dizer qual dos dois errou.
- Fale: autenticação por token JWT, sem sessão no servidor, com validade de 1 hora.

## 3. Painel (Bruno)

- Mostre os três cartões: total de doadores, aptos hoje e doações no mês.
- Fale: são 15 doadores fictícios, com doações em datas relativas a hoje.

## 4. Cadastro de doador com ficha clínica, relação 1:1 (Bruno)

- Em **Doadores**, clique em **Novo doador**.
- Mostre as duas seções, **Dados pessoais** e **Ficha clínica**, salvas de uma vez.
- Primeiro erre de propósito: CPF `111.111.111-11` e deixe o nome vazio. Mostre os erros embaixo de cada campo.
- Depois preencha com o CPF válido `529.982.247-25`, peso `60` e salve.
- Fale: um doador tem uma única ficha clínica (1:1), e a ficha só existe junto com o doador.

## 5. Registro de doação, relação 1:n (Bruno)

- No detalhe do doador recém-criado, clique em **Registrar doação**.
- Preencha pressão `120/80`, hemoglobina `13,5`, volume `450` e confirme.
- Mostre o aviso com o número da bolsa (`HV-ANO-NNNNNN`) e a linha do tempo.
- Mostre que o botão **Registrar doação** ficou desabilitado, com o motivo e a data logo abaixo.
- Fale: um doador tem várias doações (1:n), e a próxima data de doação é calculada sozinha.

## 6. Ana Souza bloqueada, a automação (Bruno)

- Em **Doadores**, abra **Ana Souza**.
- Mostre o cartão de aptidão: o motivo (intervalo mínimo de 90 dias desde a última doação) e a data a partir da qual ela volta a doar. O botão **Registrar doação** está desabilitado.
- Fale: as regras rodam todas a cada verificação (idade, peso, impedimento, tatuagem, intervalo e limite anual) e o doador volta a ficar apto sozinho quando a data chega, sem rotina agendada.
- Vá ao Swagger (http://localhost:8080/swagger-ui.html):
  1. Em `POST /api/v1/auth/login`, entre como `atendente` e copie o `token`.
  2. Clique em **Authorize**, cole o token e confirme.
  3. Em `POST /api/v1/doadores/{id}/doacoes`, use `id` = `1` e o corpo `{"pesoEmQuilos": 62, "volumeEmMililitros": 450}`.
  4. Mostre a resposta **422** com `apto`, `motivos` e `proximaDataApta`.

## 7. Segurança: atendente não exclui (Bruno)

- Na tela do sistema, saia e entre como `atendente`. Em **Doadores**, o botão **Excluir** não aparece.
- No Swagger, com o token do atendente, execute `DELETE /api/v1/doadores/{id}` com `id` = `3`. Mostre o **403**.
- Fale: o perfil `admin` herda tudo do `atendente` e ainda exclui. Mesmo sem o botão, a API recusa a operação.

## 8. Swagger com os endpoints (Bruno)

- Percorra a lista: login, doadores (listar, buscar, cadastrar, editar, excluir), doações, aptidão e painel.
- Fale: todos pedem token, exceto o login, e as respostas usam DTOs; nenhuma entidade sai pela API.

## 9. GitHub: commits e pull requests (Bruno)

- Abra o repositório: `<preencher: link do repositório>`.
- Mostre o histórico de commits e os pull requests do grupo, e o `dependabot.yml`.
- Encerre agradecendo e abrindo para perguntas.
