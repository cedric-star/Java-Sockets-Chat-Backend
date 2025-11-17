{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "java-dev-shell";

  buildInputs = with pkgs; [
    jdk21
    maven  # oder gradle
    # native dependencies für GUI / AWT / JavaFX / Swing
    xorg.libX11
    xorg.libXext
    xorg.libXrender
    xorg.libXtst
    xorg.libXi
    xorg.libXrandr
    fontconfig
    freetype
    glib
    gtk3
    cairo
    pango
  ];

  shellHook = ''
    export JAVA_HOME=${pkgs.jdk21}
    export PATH=$JAVA_HOME/bin:$PATH
    echo "Java dev shell ready — using $JAVA_HOME"
    echo "starting idea-community..."
    idea-community .
  '';
}
### Ausführen: 
# nix-shell
# idea-community .
