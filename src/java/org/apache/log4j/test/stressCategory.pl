
# This perl script all combinations of tree structures based on the "args"
# array.

# For example, if args = ("a", "b", "c"), it will output:
#
# stressTest  a b c
# stressTest  a b a.c
# stressTest  a b b.c
# stressTest  a a.b c
# stressTest  a a.b a.c
# stressTest  a a.b a.b.c

$prefix = "stressTest";

@tree = ("");

@args = ("a", "b");
permute(0, @tree);

@args = ("a", "b", "c");
permute(0, @tree);

@args = ("a", "b", "c", "d");
permute(0, @tree);

@args = ("a", "b", "c", "d", "e");
permute(0, @tree);

@args = ("a", "b", "c", "d", "e", "f");
permute(0, @tree);


sub permute() {
  my ($i, @t)  = @_;
  #print "Tree is @t\n";
  #print "i is  $i \n";
  
  if($i == $#args + 1) {
    print "$prefix @t\n";
    return;
  }
  
  foreach $j (@t) {
    #print "J is $j \n";
    #print "args[$i]=$args[$i]\n";
    if($j eq "") {
      $next = "$args[$i]";
    }
    else {
      $next = "$j.$args[$i]";
    }
	
    permute($i+1, (@t, $next));
  }
  
}
