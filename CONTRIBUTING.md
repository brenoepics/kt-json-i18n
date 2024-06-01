# Contributing to Kotlin JSON Internationalization

First off, thank you for considering contributing to Kotlin JSON Internationalization. It's people like you that make Kotlin JSON Internationalization such a great tool.

## Where do I go from here?

If you've noticed a bug or have a feature request, make sure to check our [issues](https://github.com/brenoepics/kt-json-i18n/issues) if there's something similar to what you have in mind. If there isn't, feel free to open a new issue!

## Fork & create a branch

If this is something you think you can fix, then fork Kotlin JSON Internationalization and create a branch with a descriptive name.

A good branch name would be (where issue #1 is the ticket you're working on):

```bash
git checkout -b feature-1-add-google-translator
```

## Implement your fix or feature

At this point, you're ready to make your changes! Feel free to ask for help; everyone is a beginner at first.

## Test your changes

Ensure that your code works as expected and doesn't introduce any new bugs.

## Create a pull request

At this point, you should switch back to your master branch and make sure it's up to date with Kotlin JSON Internationalization's main branch:

Go to the [Kotlin JSON Internationalization](https://github.com/brenoepics/kt-json-i18n) repo and press the "New pull request" button.

## Keeping your Pull Request updated

If a maintainer asks you to "rebase" your PR, they're saying that a lot of code has changed, and that you need to update your branch so it's easier to merge.

To learn more about rebasing in Git, there are a lot of [good](https://git-scm.com/book/en/v2/Git-Branching-Rebasing) [resources](https://www.atlassian.com/git/tutorials/merging-vs-rebasing) but here's the suggested workflow:

```bash
git checkout feature-325-add-google-translator
git pull --rebase upstream master
git push --force-with-lease origin feature-325-add-google-translator
```

## Merging a PR (maintainers only)

A PR can only be merged into master by a maintainer if:

- It is passing CI.
- It has been approved by at least two maintainers. If it was a maintainer who opened the PR, only one extra approval is needed.
- It has no requested changes.
- It is up to date with current main.

Any maintainer is allowed to merge a PR if all of these conditions are met.
