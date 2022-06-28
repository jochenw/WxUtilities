FROM almalinux:8.6

RUN dnf -y install dnf-plugins-core dnf texlive-latex texlive-metafont texlive-gsftopk texlive-ec
RUN dnf -y config-manager --set-enabled powertools
RUN dnf -y install pandoc

CMD ["/usr/sbin/init"]
