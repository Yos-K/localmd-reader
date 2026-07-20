# ADR-0014: Separate the Public Source and Private Release Repositories

Status: Accepted

## Decision

Use `Yos-K/localmd-reader` as the public source repository and
`Yos-K/localmd-reader-release` as the private release repository. Public source
CI uses no signing or Play credentials. Signed builds and Play uploads run only
in the private repository, where GitHub Environment secrets and Google Cloud
Workload Identity trust are retained.

The local checkout names the public remote `origin` and the private remote
`release`. Release operations must explicitly target the private repository.

## Alternatives Considered

- Make the existing repository public with all historical branches and PRs.
- Rewrite every historical ref and force-push the existing repository.
- Copy release secrets into a newly created public repository.
- Publish a sanitized source repository while retaining the existing repository
  as the private release boundary.

## Why This Decision

Historical development refs contain an author email that should not be public,
while the release repository contains non-exportable GitHub secrets. Separating
the boundaries preserves release configuration without exposing historical refs
or granting public workflows access to signing and Play credentials.

## Why Alternatives Were Rejected

Publishing the old repository exposes unnecessary development history. Rewriting
all GitHub PR refs is not reliably controllable. GitHub does not permit reading
secret values back through its API, and release credentials do not belong in a
public automation boundary even when encrypted by GitHub.

## Reconsider When

Reconsider when release signing moves to a dedicated external service, when the
public repository can use a separately reviewed trusted-publishing mechanism,
or when maintaining two synchronized repositories costs more than the isolation
benefit.
