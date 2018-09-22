# Decodes a Spine skeleton.atlas file
# 
# By Twan van Laarhoven <mail@twanvl.nl>
use strict;

sub getline {
  my $fh = shift;
  my $line = <$fh>;
  chomp $line;
  if ($line =~ m{^\s+([a-z]*):\s*(.*)$}) {
    return $2;
  } else {
    die "Parse error: $line\n";
  }
}


my $filename;
my $partname;
my $size;

open my $fh, '<', "skeleton.atlas";
mkdir 'images';

while (my $line = <$fh>) {
  chomp $line;
  if ($line =~ m{\.png$}) {
    $filename = $line;
    print "File: $filename\n";
  } elsif ($line =~ m{^[a-zA-Z0-9/]+$}) {
    $partname = $line;
    $partname =~ s{^images/}{};
    # parse part
    my $rotate = getline($fh);
    my $xy = getline($fh);
    my $size = getline($fh);
    my $orig = getline($fh);
    my $offset = getline($fh);
    my $index = getline($fh);
    my ($x,$y) = ($1,$2) if $xy =~ m{(\d+),\s*(\d+)};
    my ($w,$h) = ($1,$2) if $size =~ m{(\d+),\s*(\d+)};
    print "Part: $partname   at ${w}x${h}+$x+$y  rotate:$rotate\n";
    if ($rotate eq 'true') {
      `convert $filename -crop ${h}x${w}+$x+$y -rotate 90 images/$partname.png`;
    } else {
      `convert $filename -crop ${w}x${h}+$x+$y images/$partname.png`;
    }
  }
}

close $fh;
