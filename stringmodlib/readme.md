# stringmodlib

This is a collection of (currently) string transformers, all of which following
the implementation rule to have a `transform` method that takes a string and
returns one.

## what it should/could do
* We somehow have a hierarchy generic --> highly specialised transformers. This
  is not implemented consequently though. Would one ever decide to e.g.
  opensource parts of the deduplicator framework, then there could be a part of
  stringmodlib that would be of use for any person, not only a botanist. These
  guys would love 'A2BTransformer' and maybe even 'RomanNumeralTransformer' but
  would not want to have 'EpithetTransformer'.
* This is also valid for what to put into
  org.kew.stringmod.lib.transformers.RegexDefCollection; maybe more specific
  packages could have sth similar that inherits the generic one and adds to it?
* the docstrings should be made available to matchconf
* there should be an easy way (via reflection) to expose all settable fields to
  matchconf and/or other software; this would make the 'remote' configuration
  far more comfortable

