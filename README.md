# SubLoop

Aplicativo Android para cadastrar assinaturas, acompanhar valores recorrentes e receber alertas de renovação. O visual replica o arquivo **SubLoop** do Figma em Jetpack Compose, com estado inicial vazio e sem dados de demonstração.

## Stack e arquitetura

- Kotlin + Jetpack Compose
- MVVM com `StateFlow`
- Room para usuários e assinaturas
- Retrofit + coroutines para consulta de cotação
- Navigation Compose com rotas type-safe
- SharedPreferences para manter a sessão local
- SHA-256 para armazenar somente o hash da senha

Estrutura de pacotes:

```text
com.subloop
├─ network       Retrofit, API e DTOs
├─ data          Room, sessão e Repository
├─ viewmodel     ViewModels e estados de UI
└─ userinterface Compose, navegação e MainActivity
```

As telas não acessam Retrofit ou DAO diretamente. Todo acesso passa por `SubLoopRepository`, e a camada `viewmodel` não depende de `userinterface`.

## API utilizada

**Frankfurter API** — `https://api.frankfurter.app/`

A tela inicial consulta a cotação USD/BRL. Ela exibe os três estados exigidos: carregando, sucesso e erro, com opção de tentar novamente. A API é pública e não exige chave.

## Funcionalidades

- Cadastro com nome, e-mail único e senha de no mínimo 6 caracteres
- Login com mensagem genérica para credenciais inválidas
- Sessão persistente ao fechar e reabrir o app
- Saída pelo botão de perfil no cabeçalho
- Dados separados pelo usuário autenticado
- Cadastro e remoção de assinaturas
- Alertas configuráveis com notificações reais do Android
- Agendamento persistente pelo WorkManager, inclusive com o app fechado
- Solicitação da permissão de notificações no Android 13 ou superior
- Calendário nativo para escolher o vencimento, sem digitação manual da data
- Home, Assinaturas, Adicionar e Alertas na barra inferior
- Detalhe aberto por rota type-safe com o ID da assinatura
- Estado vazio real: nenhuma assinatura é pré-carregada

## Como executar

1. Abra esta pasta no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Selecione um emulador ou aparelho com Android 8.0 (API 26) ou superior.
4. Execute o módulo `app`.

Pelo terminal:

```powershell
.\gradlew.bat assembleDebug
```

O APK de desenvolvimento é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Referências visuais

- `Visão Geral (Home).png`
- `Adicionar Assinatura.png`
- `Central de Alertas.png`
- `Detalhes da Assinatura.png`

## Verificações realizadas

- `assembleDebug`: aprovado
- `testDebugUnitTest`: aprovado
- `lintDebug`: 0 erros
- Busca por Retrofit dentro de `userinterface`: vazia
- Busca por `userinterface` dentro de `viewmodel`: vazia

## Divisão de tarefas

| Integrante | O que fez |
|---|---|
| Preencher | Preencher antes da entrega |

## O que aprendemos

Preencher com uma frase de cada integrante antes da entrega.
