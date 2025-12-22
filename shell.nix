 pkgs ? import <nixpkgs> {} }:

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

    export JAVA_HOME=${pkgs.jdk21}
    echo "starting idea-cofkdsjfmunity..."
    idea-community .
  '';
}
### Ausführen: 
# nix-shell
# idea-community .
